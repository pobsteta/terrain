# terrain

Bibliothèques communes des applications de terrain de la plateforme Néméton — Marculus
(martelage), Groma (placettes), Surculus (travaux et plantations) — qui partagent le même
matériel : smartphone, récepteur GNSS en Bluetooth (ZED-F9P), corrections NTRIP Centipède,
GeoPackage hors ligne.

Licence : **GPL-3.0-or-later** (`LICENSE`).

## Modules

| Module | Contenu | Plateforme |
|---|---|---|
| `terrain-gnss` | `fr.nemeton.terrain.gnss` : NMEA (découpe du flux, GGA/GSA/GST/RMC/GSV, fix, précision, skyplot), client NTRIP (requête, réponse, sourcetable, GGA renvoyée au caster) ; `fr.nemeton.terrain.geo` : position WGS 84, aire, distance et azimut, point dans polygone, Lambert-93 ↔ WGS 84 ↔ Web Mercator | Kotlin pur, JVM 17 |

Prévus, dans cet ordre : `terrain-journal` (saisies à UUID, fusion « dernière écriture
gagne »), `terrain-gpkg`, `terrain-voix` (Vosk à grammaire fermée, push-to-talk),
`terrain-carte`.

## Utiliser

Publié par tag via [JitPack](https://jitpack.io/#pobsteta/terrain) :

```kotlin
// settings.gradle.kts
dependencyResolutionManagement {
    repositories {
        mavenCentral()
        maven("https://jitpack.io") { content { includeModule("com.github.pobsteta", "terrain") } }
    }
}

// build.gradle.kts du module
dependencies {
    implementation("com.github.pobsteta:terrain:v0.1.2") // terrain-gnss
}
```

Tant que le dépôt n'a qu'un module, JitPack le publie sous les coordonnées du dépôt
(`com.github.pobsteta:terrain`). Dès le deuxième module, chacun aura les siennes
(`com.github.pobsteta.terrain:terrain-gnss`, `…:terrain-journal`) : changer la coordonnée en
montant de version.

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
