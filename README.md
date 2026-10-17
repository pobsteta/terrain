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
| `terrain-gnss` | `fr.nemeton.terrain.gnss` : NMEA (découpe du flux, GGA/GSA/GST/RMC/GSV, fix, précision, skyplot), client NTRIP (requête, réponse, sourcetable, GGA renvoyée au caster) ; `fr.nemeton.terrain.geo` : position WGS 84, aire, distance et azimut, point dans polygone, Lambert-93 ↔ WGS 84 ↔ Web Mercator | Kotlin pur, JVM 17 |

Prévus, dans cet ordre : `terrain-gpkg`, `terrain-voix` (Vosk à grammaire fermée, push-to-talk),
`terrain-carte`.

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
    implementation("com.github.pobsteta.terrain:terrain-gnss:v0.2.0")
    implementation("com.github.pobsteta.terrain:terrain-journal:v0.2.0")
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
