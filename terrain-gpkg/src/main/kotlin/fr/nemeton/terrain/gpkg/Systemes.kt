package fr.nemeton.terrain.gpkg

import fr.nemeton.terrain.geo.Position
import fr.nemeton.terrain.geo.Projections

/**
 * Systèmes de coordonnées gérés : ceux des données forestières françaises et de la carte. Les
 * autres (UTM, anciens Lambert…) sont refusés plutôt que mal placés.
 */
enum class Systeme(val codesEpsg: Set<Int>) {
    /** Géographique en degrés : WGS 84, et ETRS89 / RGF93 confondus avec lui (écart centimétrique). */
    WGS84(setOf(4326, 4258, 4171)),
    LAMBERT93(setOf(2154)),
    WEB_MERCATOR(setOf(3857, 900913)),
    ;

    /** Point du système → position WGS 84. */
    fun versWgs84(p: Geometrie.Point): Position = when (this) {
        WGS84 -> Position(latitude = p.y, longitude = p.x)
        LAMBERT93 -> Projections.versWgs84(p.x, p.y).let { (lon, lat) -> Position(latitude = lat, longitude = lon) }
        WEB_MERCATOR -> Projections.depuisWebMercator(p.x, p.y).let { (lon, lat) -> Position(latitude = lat, longitude = lon) }
    }

    /** Position WGS 84 → point du système. */
    fun depuisWgs84(p: Position): Geometrie.Point = when (this) {
        WGS84 -> Geometrie.Point(p.longitude, p.latitude)
        LAMBERT93 -> Projections.versLambert93(p.longitude, p.latitude).let { (x, y) -> Geometrie.Point(x, y) }
        WEB_MERCATOR -> Projections.versWebMercator(p.longitude, p.latitude).let { (x, y) -> Geometrie.Point(x, y) }
    }

    companion object {
        /** Système d'une entrée de `gpkg_spatial_ref_sys` (organisation + code), null si non géré. */
        fun de(organisation: String?, code: Int?): Systeme? =
            if (organisation.equals("EPSG", ignoreCase = true) && code != null) {
                entries.firstOrNull { code in it.codesEpsg }
            } else {
                null
            }
    }
}

/** Couche dont le système de coordonnées n'est pas géré (voir [Systeme]). */
class SystemeNonGere(val table: String, val description: String) :
    IllegalArgumentException("Couche $table : système de coordonnées non géré ($description) — Lambert-93, WGS 84 ou Web Mercator attendu")
