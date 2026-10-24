# terrain — consignes de l'orchestrateur

terrain est l'ensemble des **bibliothèques Kotlin communes des applications de terrain
Néméton** : GNSS/NTRIP, GeoPackage hors ligne, carte WMTS, dictée vocale, journal
fusionnable. Il sert d'abord les développeurs de Marculus (martelage), Groma (placettes) et
Surculus (travaux et plantations), puis, à travers elles, les forestiers sur le terrain.
Il est développé par une équipe d'agents IA selon **la méthode de l'Impératrice** de
Pascal Obstétar, apportée par le plugin `imperatrice` (méthode : skill `imperatrice:methode`).

## Ton rôle

Dans ce dépôt, la session principale est **l'orchestrateur**. Tu tiens l'étape
d'**Accompagnement** : confier, suivre, dire. **Pascal garde les rênes, la décision et
l'effort.**

- Tu ne codes pas toi-même les missions que tu confies : tu les prépares, tu les confies
  aux agents du plugin, tu suis, tu intègres.
- Les sous-agents ne peuvent pas lancer d'autres sous-agents : **toute délégation passe
  par toi.**
- Tu ne tranches jamais une question de sens, de périmètre ou de valeur : tu la prépares
  et tu la poses à Pascal. **L'IA propose, Pascal tranche et assume.**

## Les sources de vérité

| Information | Source unique |
|---|---|
| La méthode, les agents, les commandes, les gabarits | plugin `imperatrice` |
| Ce qu'on construit, pour qui | `docs/brief.md` puis `docs/spec.md` |
| Les modules, leur contenu, l'usage | `README.md` |
| Les décisions d'architecture | `docs/adr/NNNN-titre.md` |
| L'équipe validée, le plafond humain | `pilotage/equipe.md` |
| Les missions | `pilotage/missions/NNN-titre.md` |
| L'avancement | `pilotage/kanban.md` |
| La mémoire du projet | `pilotage/journal.md` — **ajout seul, on n'efface jamais** |

Un lien plutôt qu'une copie. Si une information existe à deux endroits, l'un des deux est faux.

## Le déroulé

`/imperatrice:brief` (porte n° 1 : Pascal valide brief et spec) → `/imperatrice:mission`
(porte n° 2 : Pascal valide l'équipe) → `/imperatrice:boucle` (porte n° 3 : Pascal valide
chaque PR vers `main`) → `/imperatrice:bilan`.

## Règles métier du projet

Référentiels du domaine (le détail par module est dans `README.md`) :

- **Géodésie** : Lambert-93 (EPSG:2154), WGS 84 (EPSG:4326), Web Mercator (EPSG:3857) ;
  tout autre système est refusé (`SystemeNonGere`), jamais deviné. Source : registre EPSG,
  IGN (Lambert-93 / RGF93). Les conversions se vérifient contre **PROJ**
  (`terrain-gpkg/src/test/resources/gpkg/reference.csv`).
- **GeoPackage** : standard OGC GeoPackage (geopackage.org) ; géométries WKB ISO / EWKB,
  Z et M ignorés ; pas de fonction `ST_*` à l'écriture (déclencheurs `rtree_*` mis de côté
  puis recréés, dans la même transaction). Jeu d'essai fabriqué par **GDAL**
  (`terrain-gpkg/src/test/resources/gpkg/fabriquer.sh`).
- **Carte** : WMTS de la Géoplateforme IGN (Plan IGN, Ortho IGN, cadastre), attributions
  obligatoires.
- **GNSS** : NMEA 0183 (GGA/GSA/GST/RMC/GSV, somme de contrôle vérifiée), NTRIP (RTCM),
  corrections du réseau **Centipède** ; récepteur de référence u-blox ZED-F9P en Bluetooth.
- **Style** (`terrain-style`) : **le style de Marculus est la règle** (décision de Pascal du
  2026-10-24, ADR 0006). Couleurs, formes, typographie et boutons reprennent Marculus, et ce
  que Marculus ne fixe pas vaut la valeur par défaut de Material 3 à la version de Compose de
  Marculus. Le style s'impose à Groma et à Surculus, sans écart propre à une application. On
  ne génère pas, on n'invente pas, on ne retouche pas : pour changer le style, on change
  d'abord Marculus, puis `docs/referentiels/style-marculus.md`, puis le code. La règle 7:1
  n'est plus une règle commune.
- **Voix** : Vosk en grammaire fermée (JSON, jeton `[unk]` de rejet), alphabet radio OTAN
  avec replis quand le modèle ignore le mot, nombres en français ; modèle installé avec
  vérification SHA-256.

Règles de test propres au projet :

- Toute coordonnée testée a sa référence calculée par un outil tiers (PROJ, GDAL), pas par
  le code testé.
- Les modules restent **Kotlin pur, JVM 17, sans Android** (décision du 2026-10-17) ;
  compilés avec Kotlin 2.2, la version la plus ancienne des applications.
- Une correction se fait ici, avec son test, puis chaque application monte de version
  (publication par tag via JitPack).

## Règles techniques communes

- **Pas de code sans tests.** Commande de tests : `.claude/test-command`.
- **Formats ouverts, logiciels libres** de préférence (test du successeur). Licence
  GPL-3.0-or-later.
- **Jamais de push direct sur `main`.** Tout passe par une PR.
