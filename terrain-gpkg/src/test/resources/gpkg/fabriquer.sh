#!/bin/sh
# Fabrique essai.gpkg et sa référence WGS 84 (reference.csv) avec GDAL/OGR et PROJ.
# Usage : ./fabriquer.sh <exemple.gpkg de Marculus>   (à relancer seulement pour changer le jeu)
set -e
cd "$(dirname "$0")"
EXEMPLE="$1"
T=$(mktemp -d)
rm -f essai.gpkg
# 1. Parcelles réelles (MULTIPOLYGON, Lambert-93), table « parcelle ».
ogr2ogr -f GPKG essai.gpkg "$EXEMPLE" parcelle -nln parcelle
# 2. Dessertes : LINESTRING et MULTILINESTRING en Lambert-93, avec nom / nature et un nul.
cat > $T/desserte.csv <<'CSV'
WKT,nom,nature,ouverte
"LINESTRING (853400 6693000,853450 6693050,853500 6693040)",Route forestière du Chêne,route empierrée,true
"MULTILINESTRING ((853300 6692900,853350 6692950),(853360 6692960,853400 6693000))",,piste,false
CSV
ogr2ogr -f GPKG -update essai.gpkg $T/desserte.csv -nln desserte -a_srs EPSG:2154 \
  -oo GEOM_POSSIBLE_NAMES=WKT -oo KEEP_GEOM_COLUMNS=NO -oo AUTODETECT_TYPE=YES -nlt PROMOTE_TO_MULTI -nlt CONVERT_TO_LINEAR
# 3. Houppiers : POLYGON avec trou, attribut h_max réel.
cat > $T/houppier.csv <<'CSV'
WKT,h_max
"POLYGON ((853410 6693010,853416 6693010,853416 6693016,853410 6693016,853410 6693010),(853412 6693012,853413 6693012,853413 6693013,853412 6693012))",24.5
CSV
ogr2ogr -f GPKG -update essai.gpkg $T/houppier.csv -nln houppier -a_srs EPSG:2154 \
  -oo GEOM_POSSIBLE_NAMES=WKT -oo KEEP_GEOM_COLUMNS=NO -oo AUTODETECT_TYPE=YES -nlt POLYGON
# 4. Points en WGS 84, en Web Mercator, en 3D (Lambert-93 + Z), et une couche en UTM 31N (refusée).
cat > $T/wgs.csv <<'CSV'
WKT,nom
"POINT (5.0311905006 47.3198242146)",Dijon
CSV
ogr2ogr -f GPKG -update essai.gpkg $T/wgs.csv -nln points_wgs84 -a_srs EPSG:4326 -oo GEOM_POSSIBLE_NAMES=WKT -oo KEEP_GEOM_COLUMNS=NO -nlt POINT
ogr2ogr -f GPKG -update essai.gpkg $T/wgs.csv -nln points_wm -s_srs EPSG:4326 -t_srs EPSG:3857 -oo GEOM_POSSIBLE_NAMES=WKT -oo KEEP_GEOM_COLUMNS=NO -nlt POINT
cat > $T/z.csv <<'CSV'
WKT,nom
"POINT Z (853412.31 6693028.77 312.4)",borne
CSV
ogr2ogr -f GPKG -update essai.gpkg $T/z.csv -nln points_3d -a_srs EPSG:2154 -oo GEOM_POSSIBLE_NAMES=WKT -oo KEEP_GEOM_COLUMNS=NO -nlt POINTZ
ogr2ogr -f GPKG -update essai.gpkg $T/wgs.csv -nln points_utm -s_srs EPSG:4326 -t_srs EPSG:32631 -oo GEOM_POSSIBLE_NAMES=WKT -oo KEEP_GEOM_COLUMNS=NO -nlt POINT
# Référence : chaque sommet reprojeté en WGS 84 par PROJ (couche;fid;partie;index;lon;lat).
echo "couche;fid;partie;index;lon;lat" > reference.csv
for c in parcelle desserte houppier points_wgs84 points_wm points_3d; do
  ogr2ogr -f GeoJSON $T/$c.json essai.gpkg $c -t_srs EPSG:4326 -preserve_fid -lco COORDINATE_PRECISION=12 -lco RFC7946=NO
  python3 -I - "$c" "$T/$c.json" >> reference.csv <<'PY'
import json, sys
couche, chemin = sys.argv[1], sys.argv[2]
def parties(g):
    t, c = g["type"], g["coordinates"]
    if t == "Point": return [[c]]
    if t == "LineString": return [c]
    if t == "Polygon": return c
    if t == "MultiLineString": return c
    if t == "MultiPolygon": return [a for p in c for a in p]
    raise SystemExit(t)
for f in json.load(open(chemin))["features"]:
    for k, partie in enumerate(parties(f["geometry"])):
        for i, v in enumerate(partie):
            print(f"{couche};{f['id']};{k};{i};{v[0]!r};{v[1]!r}")
PY
done
rm -rf $T
