# terrain

Bibliothèques communes des applications de terrain de la plateforme Néméton — Marculus
(martelage), Groma (placettes), Surculus (travaux et plantations) — qui partagent le même
matériel : smartphone, récepteur GNSS en Bluetooth (ZED-F9P), corrections NTRIP Centipède,
GeoPackage hors ligne.

Licence : **GPL-3.0-or-later** (`LICENSE`).

## Modules

| Module | Contenu | Plateforme |
|---|---|---|
| `terrain-journal` | `fr.nemeton.terrain.journal` : `Fusion` (union sur l'UUID, « dernière écriture gagne » sur `modifie`, la locale à égalité, rien d'effacé), `HorlogeMonotone` (`modifie` strictement croissant), `Identifiants` (UUID v4) | Kotlin pur, JVM 17 |
| `terrain-gpkg` | `fr.nemeton.terrain.gpkg` : couches vecteur d'un GeoPackage en SQL simple (`GeoPackage` : couches, entités, insertion), format des géométries (`FormatGpkg`, WKB ISO / EWKB, Z et M ignorés), systèmes `Systeme` (Lambert-93, WGS 84, Web Mercator ; les autres refusés par `SystemeNonGere`) | Kotlin pur, JVM 17 ; l'application fournit une `BaseSqlite` |
| `terrain-voix` | `fr.nemeton.terrain.voix` : `NoyauPtt` (push-to-talk : une seule session quelle que soit la source tenue, bip obligatoire, coupure pendant une annonce TTS), `NombresFrancais`, `AlphabetRadio` (replis quand le modèle ignore le mot OTAN), `GrammaireFermee` (JSON Vosk, jeton `[unk]` de rejet), `VocabulaireVosk` (mots connus du modèle, lus dans `graph/Gr.fst`), `InstallationModele` (téléchargement ou archive locale, SHA-256, témoin de fin) | Kotlin pur, JVM 17 |
| `terrain-carte` | `fr.nemeton.terrain.carte` : couches WMTS de la Géoplateforme (`Geoplateforme` : Plan IGN, Ortho IGN, cadastre ; URL d'une tuile), `Attributions`, `Echelle` (résolution au sol, distance ronde, libellé), `Emprise` / `Tuile` / `ZoneTuiles` (tuiles à télécharger pour le hors-ligne), `GrilleWebMercator` (ortho déjà sur la grille XYZ) | Kotlin pur, JVM 17 |
| `terrain-style` | `fr.nemeton.terrain.style` : `Contraste` (luminance relative et rapport de contraste WCAG 2.2 sur des ARGB `0xFFRRGGBB` en `Long` ; toute autre valeur refusée par `IllegalArgumentException`) | Kotlin pur, JVM 17 |
| `terrain-gnss` | `fr.nemeton.terrain.gnss` : NMEA (découpe du flux, GGA/GSA/GST/RMC/GSV, fix, précision, skyplot), client NTRIP (requête, réponse, sourcetable, GGA renvoyée au caster) ; `fr.nemeton.terrain.geo` : position WGS 84, aire, distance et azimut, point dans polygone, Lambert-93 ↔ WGS 84 ↔ Web Mercator | Kotlin pur, JVM 17 |

Restent dans chaque application (Android, décision du 2026-10-17) : l'affichage osmdroid de la
carte (une `OnlineTileSourceBase` d'une ligne autour de `CoucheWmts.url`), l'enveloppe Vosk
(`SpeechService`, `Recognizer`), l'adaptateur `BaseSqlite`, et la grammaire métier de la dictée.

## Utiliser

Publié par tag via [JitPack](https://jitpack.io/#pobsteta/terrain) :

```kotlin
// settings.gradle.kts
dependencyResolutionManagement {
    repositories {
        mavenCentral()
        maven("https://jitpack.io") { content { includeGroup("com.github.pobsteta.terrain") } }
    }
}

// build.gradle.kts du module
dependencies {
    implementation("com.github.pobsteta.terrain:terrain-gnss:v0.5.0")
    implementation("com.github.pobsteta.terrain:terrain-journal:v0.5.0")
    implementation("com.github.pobsteta.terrain:terrain-gpkg:v0.5.0") // apporte terrain-gnss
    implementation("com.github.pobsteta.terrain:terrain-voix:v0.5.0")
    implementation("com.github.pobsteta.terrain:terrain-carte:v0.5.0")
}
```

Jusqu'à v0.1.2, le dépôt n'avait qu'un module, publié par JitPack sous
`com.github.pobsteta:terrain` ; depuis v0.2.0, chaque module a ses coordonnées.

Les versions v0.1.0 et v0.1.1 n'existent pas sur JitPack : Maven Central refusait (HTTP 429) les
machines de JitPack, d'où le miroir Google en tête de `settings.gradle.kts`.

```kotlin
import fr.nemeton.terrain.gnss.NmeaParser
import fr.nemeton.terrain.gnss.QualiteFix

val gga = NmeaParser.parseGga(phrase) ?: return   // null si somme de contrôle fausse
val fix = NmeaParser.fixDepuis(gga)
if (fix.qualite == QualiteFix.RTK_FIXE) { /* position centimétrique */ }
```

### GeoPackage sur Android

`terrain-gpkg` ne dépend pas d'Android : l'application branche son `SQLiteDatabase` sur
l'interface `BaseSqlite` (une trentaine de lignes : `rawQuery` → liste de maps `Long` / `Double` /
`String` / `ByteArray`, `execSQL`, `beginTransaction…`). Les tests JVM font la même chose avec
sqlite-jdbc (`terrain-gpkg/src/test/.../SqliteJdbc.kt`).

L'écriture n'utilise aucune fonction `ST_*` : les déclencheurs de l'index spatial (`rtree_*`,
posés par GDAL et QGIS, qui appellent `ST_IsEmpty`…) sont mis de côté pendant l'insertion, la
ligne d'index est écrite à la main, puis ils sont recréés, dans la même transaction. Les tuiles
(ortho) ne sont pas traitées.

Jeu d'essai : `terrain-gpkg/src/test/resources/gpkg/essai.gpkg`, fabriqué par GDAL
(`fabriquer.sh`), avec la référence PROJ de chaque sommet en WGS 84 (`reference.csv`).

Compilé avec Kotlin 2.2 (la version la plus ancienne des applications) : lisible par
Kotlin 2.2 et suivants.

## Développer

```sh
./gradlew test                 # tests JVM
./gradlew publishToMavenLocal  # version « dev » dans ~/.m2, pour essayer dans une appli
```

Une correction se fait ici, avec son test, puis chaque application monte de version.

## Origine

Code extrait de Marculus (`fr.marculus.core`, octobre 2026) ; projections Lambert-93 et
distance/azimut repris de Groma (`groma.core`). Mêmes auteurs, même licence.
