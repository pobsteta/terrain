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
    implementation("com.github.pobsteta.terrain:terrain-gnss:v0.6.0")
    implementation("com.github.pobsteta.terrain:terrain-journal:v0.6.0")
    implementation("com.github.pobsteta.terrain:terrain-gpkg:v0.6.0") // apporte terrain-gnss
    implementation("com.github.pobsteta.terrain:terrain-voix:v0.6.0")
    implementation("com.github.pobsteta.terrain:terrain-carte:v0.6.0")
    implementation("com.github.pobsteta.terrain:terrain-style:v0.6.0")
}
```

### Publier une version

Pascal pose le tag `vX.Y.Z` **sur un commit de `main`** ; la CI rejoue les tests sur le tag et
échoue si le commit n'est pas sur `main` (ADR 0003). JitPack construit à la première demande,
sans les tests. Une application qui épingle par sécurité utilise le **SHA court** de ce commit
comme version (`com.github.pobsteta.terrain:terrain-style:<sha court>`), compte ses 7 jours
depuis la première construction JitPack (après quoi l'artefact est immuable) et fige la somme
de contrôle (`verification-metadata.xml`).

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

### Couleurs : `terrain-style`

`StyleTerrain.clair` et `StyleTerrain.sombre` sont deux `Schema`, un champ par rôle de
`ColorScheme` de Compose Material 3 (48 rôles, en ARGB `0xFFRRGGBB` dans un `Long`), à recopier
dans le thème de l'application. Ils sont générés une fois par Material Color Utilities (ADR 0001 et
0004 : VertMarculus `#374742`, variante `CONTENT`, `SPEC_2021`, contraste 1,0) puis figés ; le
module publié ne dépend pas de MCU. Nuancier : `terrain-style/generation/nuancier.svg`.

Couleurs d'état communes (ADR 0002), chacune une `Semantique` (`couleur`, `surCouleur`,
`conteneur`, `surConteneur`) de chaque schéma ; l'erreur est `error*` du schéma, sans doublon :

- `reussi` : **valeur provisoire jusqu'à l'essai au soleil du 2026-10-30** (mesure de Pascal ; elle
  peut changer en v0.6.1, voir « Changer le vert de départ de `reussi` »). Généré par MCU avec la
  palette de `#0F8558` (RAL 6032, non harmonisé) à la place de la
  palette d'erreur, donc avec les tons et les courbes de contraste d'`error` ; l'écart de teinte
  HCT à `primary`, mesuré à chaque génération, est inscrit en tête de `StyleTerrain.kt` ;
- `alerte` : figée hors MCU, jaune de sécurité `#E2AA00` (ISO 3864-4, Table 1, β 0,45) en
  `couleur` et `conteneur`, noir `#000000` dessus (9,98:1), dans les deux thèmes.

Rôles interdits en texte (sous 7:1 sur l'un de leurs fonds, liste `interditsEnTexte` de
chaque schéma, calculée par le script) : `alerte.couleur`, par décision (ADR 0002), en clair
comme en sombre ; aucun parmi les 48 rôles ni parmi les autres jetons d'état. Le jaune sert de
fond, jamais de texte.

Compilé avec Kotlin 2.2 (la version la plus ancienne des applications) : lisible par
Kotlin 2.2 et suivants.

## Développer

```sh
./gradlew test                 # tests JVM
./gradlew publishToMavenLocal  # version « dev » dans ~/.m2, pour essayer dans une appli
./gradlew :terrain-style:generer        # régénère les jetons de couleur (ADR 0001)
./gradlew :terrain-style:testGeneration # tests du script de génération
```

Une correction se fait ici, avec son test, puis chaque application monte de version.

**Deux commandes, pas une.** `.claude/test-command` (et donc le travail des agents) ne lance
que `./gradlew test`, qui **ne joue pas** `testGeneration` ; la CI, elle, joue les trois
(`test`, `generer` suivi d'un contrôle d'absence de différence, `testGeneration` : voir
`.github/workflows/ci.yml`). Avant toute PR qui touche `terrain-style`, lancer à la main
`./gradlew test` **et** `./gradlew :terrain-style:testGeneration`.

### Changer le vert de départ de `reussi` (par exemple le repli `#00843D`)

**`#0F8558` est provisoire jusqu'à l'essai au soleil du 2026-10-30** (mesure de Pascal) ; après
l'essai, une note datée de l'ADR 0002 le gèle (garder `#0F8558` ou passer au repli). Les mentions
de `#0F8558` ci-dessous suivent cette réserve.

Cas prévu et **autorisé d'avance** par la décision de Pascal (mission 007) : repli `#00843D` si
l'essai au soleil confond `reussi` et `primary`. Aucune nouvelle ADR n'est requise ; le passage
au repli se consigne par une note datée dans l'ADR 0002 (étape 5). **Avertissement** : `#00843D`
(vert ANSI Z535.1) sortirait de la région verte d'ISO 3864-4, alors que `#0F8558` y est ; calcul
approché du relecteur, non rejoué par l'acquéreur (`docs/referentiels/couleurs-etat.md`,
ligne 113). Le repli quitte donc la norme qui fonde le choix actuel : Pascal le sait en le
décidant.

1. Dans `terrain-style/generation/src/fr/nemeton/terrain/style/generation/Generer.kt` :
   `Reglages.REUSSI` (ligne 50, `0xFF0F8558L`) et `Reglages.SOURCE_REUSSI` (ligne 51, texte de
   la source, repris en tête de `StyleTerrain.kt`) ; relire aussi le commentaire au-dessus
   (lignes 44-49), qui cite RAL 6032.
2. Dans `terrain-style/generation/test/fr/nemeton/terrain/style/generation/GenererTest.kt`,
   le test « reussi a pour palette celle de 0F8558… » (lignes 124-125) affirme
   `assertEquals(0xFF0F8558L, Reglages.REUSSI)`. **Cette assertion échouera exprès** : elle
   garde la valeur décidée. À mettre à jour avec la nouvelle valeur, en PR et sur décision de
   Pascal, jamais pour « faire passer » un test (ce fichier n'est pas à modifier à la légère).
3. `./gradlew :terrain-style:generer` : réécrit `StyleTerrain.kt`, `jetons.csv` et
   `nuancier.svg` (ne pas les modifier à la main : la CI régénère et échoue sur toute
   différence).
4. `./gradlew test` puis `./gradlew :terrain-style:testGeneration`. Si un couple tombe sous
   7:1, le test le liste et le rôle passe « interdit en texte » ; on ne retouche pas la valeur à
   la main. Mettre alors à jour le paragraphe « Rôles interdits en texte » de ce README.
5. Mentions de `#0F8558` à mettre à jour hors du code généré : ce README (section
   `terrain-style`, ligne `reussi`) et, par une note datée qui **ajoute** sans effacer, l'ADR
   0002. `docs/brief.md` (arbitrages datés) garde la valeur décidée à sa date : on n'y touche pas.
6. PR vers `main` (jamais de push direct), CI verte, relecture, puis **Pascal pose le tag**
   `vX.Y.Z` sur le commit de `main` (voir « Publier une version »).

### Refaire la table de référence des contrastes

Seulement si l'on ajoute ou change des couples de
`terrain-style/src/test/resources/contraste/reference.csv` (changer le vert de `reussi` ne le
demande pas). Prérequis : **Node ≥ 18** et **Python 3**. La marche à suivre (installation de
`color2k` et `culori` à versions épinglées, commande) est dans l'en-tête de
`terrain-style/src/test/resources/contraste/fabriquer.py`. Les deux outils tiers calculent ;
le code de `terrain-style` n'intervient pas.

## Origine

Code extrait de Marculus (`fr.marculus.core`, octobre 2026) ; projections Lambert-93 et
distance/azimut repris de Groma (`groma.core`). Mêmes auteurs, même licence.
