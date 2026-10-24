# terrain

Bibliothèques communes des applications de terrain de la plateforme Néméton — Marculus
(martelage), Groma (placettes), [Surculus](https://github.com/pobsteta/surculus) (travaux et
plantations) — qui partagent le même matériel : smartphone, récepteur GNSS en Bluetooth
(ZED-F9P), corrections NTRIP Centipède, GeoPackage hors ligne.

Licence : **GPL-3.0-or-later** (`LICENSE`).

## Modules

| Module | Contenu | Plateforme |
|---|---|---|
| `terrain-journal` | `fr.nemeton.terrain.journal` : `Fusion` (union sur l'UUID, « dernière écriture gagne » sur `modifie`, la locale à égalité, rien d'effacé), `HorlogeMonotone` (`modifie` strictement croissant), `Identifiants` (UUID v4) | Kotlin pur, JVM 17 |
| `terrain-gpkg` | `fr.nemeton.terrain.gpkg` : couches vecteur d'un GeoPackage en SQL simple (`GeoPackage` : couches, entités, insertion), format des géométries (`FormatGpkg`, WKB ISO / EWKB, Z et M ignorés), systèmes `Systeme` (Lambert-93, WGS 84, Web Mercator ; les autres refusés par `SystemeNonGere`) | Kotlin pur, JVM 17 ; l'application fournit une `BaseSqlite` |
| `terrain-voix` | `fr.nemeton.terrain.voix` : `NoyauPtt` (push-to-talk : une seule session quelle que soit la source tenue, bip obligatoire, coupure pendant une annonce TTS), `NombresFrancais`, `AlphabetRadio` (replis quand le modèle ignore le mot OTAN), `GrammaireFermee` (JSON Vosk, jeton `[unk]` de rejet), `VocabulaireVosk` (mots connus du modèle, lus dans `graph/Gr.fst`), `InstallationModele` (téléchargement ou archive locale ; le modèle en place survit à tout échec ; `reprendre()` avant de charger le modèle ; empreinte explicite `Empreinte.Sha256` ou `Empreinte.NonControlee` ; erreurs typées) | Kotlin pur, JVM 17 |
| `terrain-carte` | `fr.nemeton.terrain.carte` : couches WMTS de la Géoplateforme (`Geoplateforme` : Plan IGN, Ortho IGN, cadastre ; URL d'une tuile), `Attributions`, `Echelle` (résolution au sol, distance ronde, libellé), `Emprise` / `Tuile` / `ZoneTuiles` (tuiles à télécharger pour le hors-ligne), `GrilleWebMercator` (ortho déjà sur la grille XYZ) | Kotlin pur, JVM 17 |
| `terrain-style` | `fr.nemeton.terrain.style` : le style de Marculus (ADR 0006) : `StyleTerrain` (48 rôles de couleur, clair et sombre, plus `reussi` et `alerte`), `FormesTerrain`, `TypoTerrain`, `BoutonsTerrain`, et `Contraste` (rapport de contraste WCAG 2.2 sur des ARGB `0xFFRRGGBB` en `Long` ; toute autre valeur refusée par `IllegalArgumentException`) | Kotlin pur, JVM 17 |
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
    implementation("com.github.pobsteta.terrain:terrain-style:v0.7.1")
}
```

Exemple d'usage (`terrain-gnss`) :

```kotlin
import fr.nemeton.terrain.gnss.NmeaParser
import fr.nemeton.terrain.gnss.QualiteFix

val gga = NmeaParser.parseGga(phrase) ?: return   // null si somme de contrôle fausse
val fix = NmeaParser.fixDepuis(gga)
if (fix.qualite == QualiteFix.RTK_FIXE) { /* position centimétrique */ }
```

### Modèle Vosk : `terrain-voix`

`InstallationModele` installe un modèle Vosk à grammaire dynamique (ADR 0005, spec de
`terrain-voix`). **Le modèle en place survit à tout échec** : le nouveau est décompressé dans
`<nom>.nouveau`, vérifié, écrit sur le disque et marqué d'un témoin, puis la bascule se fait en
deux renommages. Une reprise, au début de chaque opération et sous verrou, termine ou défait une
bascule interrompue (processus tué, batterie vide). **Avant de charger le modèle**, l'application
appelle `reprendre()` hors du fil principal : une bascule interrompue est remise dans le dossier
du modèle, là où Vosk le cherche.

```kotlin
import fr.nemeton.terrain.voix.*

val installation = InstallationModele(
    File(filesDir, "vosk-model-small-fr-0.22"),           // à côté : .nouveau, .ancien, .zip, .verrou
    "https://alphacephei.com/vosk/models/vosk-model-small-fr-0.22.zip",
    Empreinte.Sha256("…64 caractères hexadécimaux…"),     // ou Empreinte.NonControlee, écrit en toutes lettres
    espaceDisponible = { storageManager.getAllocatableBytes(storageManager.getUuidForPath(it)) },
)
installation.reprendre()                                  // hors du fil principal, avant de charger le modèle
if (!installation.estInstalle) {                          // lecture pure, sûre sur le fil principal
    installation.telecharger(progression = { … }, continuer = { !annule })  // hors du fil principal
        .onFailure { e ->
            when (e) {
                is EspaceInsuffisant -> "Il manque ${e.manquant} octets"
                is EmpreinteInattendue -> "Archive altérée (attendue ${e.attendue}, obtenue ${e.obtenue})"
                is ArchiveIllisible -> "Archive ou réseau en défaut : ${e.raison}"
                is StructureInvalide -> "Modèle incomplet : ${e.manquant}"
                is SansGrammaireDynamique -> "Modèle sans grammaire dynamique (HCLG.fst)"
                is Interrompu -> "Annulé"
                else -> "Erreur d'entrée-sortie : ${e.message}"   // IOException : le modèle en place reste
            }
        }
}
// Import par fichier : installation.installer(archive, continuer = { !annule })
```

- **Modèle valide** : `am/final.mdl`, `conf/model.conf`, `graph/HCLr.fst`, `graph/Gr.fst`,
  `graph/disambig_tid.int`, sous une racine détectée (dossier de tête ou non ; `__MACOSX/` et
  `.DS_Store` ignorés) ; tout `graph/HCLG.fst` est refusé (`SansGrammaireDynamique`).
- **Espace** : l'installation demande la taille décompressée plus une marge de 32 Mio
  (`InstallationModele.MARGE_ESPACE`) ; le téléchargement demande `Content-Length` plus la marge
  avant de commencer. Pour `vosk-model-small-fr-0.22`, prévoir 144 271 562 octets libres avant un
  téléchargement (archive, modèle décompressé et marge), 102 038 239 pour un import.
- **Refus** (`ArchiveIllisible`) : zip illisible ou tronqué, CRC-32 faux, entrée hors du dossier,
  à segment `..`, absolue, en double ou dont le nom n'est pas en UTF-8, plusieurs modèles, réseau
  en défaut.
- Une entrée `.installe` de l'archive est ignorée : le seul témoin est celui que pose
  l'installation. Si seul l'effacement final de l'ancien modèle échoue, l'installation réussit ;
  la reprise suivante l'efface.
- Le verrou est partagé par le processus : un appel depuis un autre fil attend. Un appel imbriqué
  depuis `progression` ou `continuer` obtient `OverlappingFileLockException` dans son `Result`.
- `<nom>.verrou` n'est jamais effacé ; `supprimer()` efface le modèle et ses restes.

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

### Style : `terrain-style`

`terrain-style` est **le style de Marculus** (ADR 0006) : couleurs, formes, typographie et boutons,
recopiés du référentiel `docs/referentiels/style-marculus.md` (Marculus v0.65.0 et Compose
Material 3 1.4.0), sans rien générer ni calculer. Kotlin pur : ni Compose ni Android ; l'application
recopie les valeurs dans son thème.

- `StyleTerrain.clair` et `StyleTerrain.sombre` : deux `Schema`, un champ par rôle de `ColorScheme`
  (48 rôles, ARGB `0xFFRRGGBB` dans un `Long`). `primary` est le VertVif de Marculus `#3E7A40`,
  `primaryContainer` son VertMarculus `#374742` ; le reste est le défaut de Material 3.
  Nuancier : `terrain-style/generation/nuancier.svg`.
- `FormesTerrain` : les 5 rayons de `Shapes` en dp (`Float`) et `pilulePourcent` (50, forme des boutons).
- `TypoTerrain` : les 15 styles de `Typography`, chacun un `StyleTexte` (taille et interligne en sp,
  graisse, interlettrage).
- `BoutonsTerrain` : dimensions des boutons en dp (hauteur 40, largeur 58, marges, icône, FAB, puces…).
- `Contraste` : rapport de contraste WCAG 2.2, pour **mesurer** ; le style n'impose aucun seuil.

Couleurs d'état communes (ADR 0002), chacune une `Semantique` (`couleur`, `surCouleur`,
`conteneur`, `surConteneur`) de chaque schéma ; l'erreur est `error*` du schéma :

- `reussi` : `#0F8558` est définitif (essai au soleil de Pascal du 2026-10-24) ; valeurs de
  v0.6.1 gelées (ADR 0002) ;
- `alerte` : jaune de sécurité `#E2AA00` (ISO 3864-4) en `couleur` et `conteneur`, noir `#000000`
  dessus, dans les deux thèmes.

`interditsEnTexte` vaut `{"alerte.couleur"}` dans chaque schéma : seul `alerte.couleur` est interdit en texte, par décision (ADR 0002) : le
jaune sert de fond, jamais de texte.

Compilé avec Kotlin 2.2 (la version la plus ancienne des applications) : lisible par
Kotlin 2.2 et suivants.

## Développer

```sh
./gradlew test                 # tests JVM
./gradlew publishToMavenLocal  # version « dev » dans ~/.m2, pour essayer dans une appli
python3 -I terrain-style/generation/nuancier.py  # redessine le nuancier depuis le CSV
```

Une correction se fait ici, avec son test, puis chaque application monte de version. La CI joue
`./gradlew test`, redessine le nuancier et échoue sur toute différence.

### Changer le style

Le style est celui de Marculus ; on change dans cet ordre, jamais dans l'autre.

1. **Marculus** : la valeur change d'abord dans l'application Marculus (ou dans Material 3).
2. **Le référentiel** : relever la nouvelle valeur, avec sa source et sa version, dans
   `docs/referentiels/style-marculus.md` ; pour une couleur, aussi dans le CSV
   `terrain-style/src/test/resources/jetons/style-marculus.csv`. Pascal décide.
3. **Le code** : recopier la valeur dans `StyleTerrain.kt`, `FormesTerrain.kt`, `TypoTerrain.kt`
   ou `BoutonsTerrain.kt` ; les tests (`StyleMarculusTest`, `FormesTerrainTest`, `TypoTerrainTest`,
   `BoutonsTerrainTest`) comparent au référentiel et échouent s'ils divergent.
4. `python3 -I terrain-style/generation/nuancier.py`, puis `./gradlew test`.
5. PR vers `main` (jamais de push direct), CI verte, relecture, puis **Pascal pose le tag**
   `vX.Y.Z` (voir « Publier une version »).

`reussi` et `alerte` ne viennent pas de Marculus : les changer demande une décision de Pascal et une
note datée de l'ADR 0002.

### Publier une version

Pascal pose le tag `vX.Y.Z` **sur un commit de `main`** ; la CI rejoue les tests sur le tag et
échoue si le commit n'est pas sur `main` (ADR 0003). JitPack construit à la première demande,
sans les tests. Une application qui épingle par sécurité utilise le **SHA court** de ce commit
comme version (`com.github.pobsteta.terrain:terrain-style:<sha court>`), compte ses 7 jours
depuis la première construction JitPack (après quoi l'artefact est immuable) et fige la somme
de contrôle (`verification-metadata.xml`).

Le vert de départ de `reussi` (`#0F8558`) est gelé depuis l'essai au soleil du 2026-10-24 ;
aucune version ne le change d'avance (ADR 0002).

Jusqu'à v0.1.2, le dépôt n'avait qu'un module, publié par JitPack sous
`com.github.pobsteta:terrain` ; depuis v0.2.0, chaque module a ses coordonnées.

Les versions v0.1.0 et v0.1.1 n'existent pas sur JitPack : Maven Central refusait (HTTP 429) les
machines de JitPack, d'où le miroir Google en tête de `settings.gradle.kts`.

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
