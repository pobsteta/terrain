# Référentiel — Material Color Utilities (MCU) et Compose Material 3

- **Propriétaire :** Pascal Obstétar
- **Acquis par :** `acquereur`, mission 001 (issue #1)
- **Date de consultation :** 2026-10-23
- **Décisions éclairées :** ADR 0001 (comment exécuter MCU) et ADR 0002 (réussi par `errorPalette`, comportement des `*Dim`).
  Ce fichier rend des constats ; il ne tranche rien.
- **Statut au 2026-10-23 :** constats, matière des ADR 0001 et 0002 (décidés) ; ce fichier ne tranche rien.
- **Mission redite (aller-retour 1/3) :** établir, sources primaires à l'appui, les faits MCU et
  Compose Material 3 dont dépendent les ADR 0001 et 0002, afin que Pascal tranche et que le `dev`
  sache quoi compiler ; décisions éclairées nommées ci-dessus, aucune tranchée ici.
- **Méthode de lecture :** première lecture par WebFetch (un petit modèle résume la page) ; **à
  l'aller-retour 1, tous les numéros de ligne et les imports ont été relus avec Read/Grep sur une
  copie locale du dépôt au commit `bd811e4`** (HEAD de la copie vérifié dans `.git`). Les numéros
  de ligne ci-dessous sont ceux de cette relecture ; les URL sont épinglées au SHA. Les extraits de
  code sont ceux des sources Apache-2.0 (Copyright Google LLC).
- **Ce qui n'est PAS fait ici :** parité Java/Kotlin **par exécution** (mission 006) ; compilation
  des sources Kotlin avec Kotlin 2.2 (mission 006).
- **Corrections de l'aller-retour 1 :** E1 (CMF), E3 et §1.3 (huit fichiers Java à annotations),
  numéros de ligne (liste du relecteur et revérification complète), imports des 43 `.kt` lus en
  entier, `ContrastCurve.get`, jar Compose 1.4.0.
- **Corrections de l'aller-retour 2 :** commit `bd811e4` dit « retenu par Pascal » (ADR 0001, commit `a9da41d`) ;
  jar Compose 1.4.0 lu (`ColorScheme.kt` décompressé, sha256 du jar
  `3e3209422a382566a780e2c1410edd866e4474498098fdbcf942c690ade1ddaa`, vérifié par l'orchestrateur et le relecteur) ;
  point au sol 1 corrigé ; ancien point au sol 2 remplacé.

## Écarts relevés (à lire d'abord)

| # | Écart | Entre quoi et quoi | Cause / suite |
|---|---|---|---|
| E1 | `maybeFallbackSpecVersion` est plus fin que l'ADR 0001 : (a) `CMF` garde **toujours** la version demandée, sans aucun repli ; (b) `EXPRESSIVE`, `VIBRANT`, `TONAL_SPOT`, `NEUTRAL`, et `CONTENT` sur `Platform.WATCH`, gardent la version demandée, sauf `SPEC_2026` ramenée à `SPEC_2025` ; (c) toute autre combinaison (`FIDELITY`, `MONOCHROME`, `RAINBOW`, `FRUIT_SALAD`, `CONTENT` sur téléphone) retombe à `SPEC_2021`. | ADR 0001, §« Version de spec » ↔ `DynamicScheme.kt` L399-417 et `DynamicScheme.java` L216-229 (mêmes cas) | Le constat utile à l'ADR tient : `FIDELITY` ⇒ `SPEC_2021`, quel que soit le `specVersion` demandé. Corriger la phrase de l'ADR. |
| E2 | `errorPalette` : en Java, paramètre `Optional<TonalPalette>`, défaut 25/84 appliqué dans le corps (`DynamicScheme.java` L189). En Kotlin, paramètre **obligatoire, sans défaut** (`DynamicScheme.kt` L52) ; le 25/84 vient de `ColorSpec2021.getErrorPalette` (Kotlin L1244-1252 : `TonalPalette.fromHueAndChroma(25.0, 84.0)` ; Java L1451-1472 : `Optional.empty()` pour les neuf variantes acceptées, exception pour `CMF`). | ADR 0002 (« par défaut teinte 25, chroma 84 ») ↔ Kotlin | Mêmes valeurs finales ; le `dev` Kotlin doit **passer** la palette, il n'y a pas de repli au constructeur. |
| E3 | Huit fichiers Java importent `androidx.annotation` ou `errorprone` (grep `^import (androidx\|com\.google)` sur `java/`, commit `bd811e4`) : `dynamiccolor/ColorSpec.java` L19-20 ; `ColorSpec2021.java` L22-24 (`NonNull`, `Nullable`, `com.google.errorprone.annotations.Var`) ; `ColorSpec2025.java` L32-33 ; `ColorSpec2026.java` L24 ; `DynamicColor.java` L19-21 (`NonNull`, `Nullable`, `errorprone.CanIgnoreReturnValue`) ; `MaterialDynamicColors.java` L19-20 ; `ToneDeltaPair.java` L19 ; `scheme/Scheme.java` L21-22 (`errorprone.CanIgnoreReturnValue`, `CheckReturnValue`). Les 46 − 8 = 38 autres `.java` n'en importent aucun. | ADR 0001, option 2 | Les sources Java ne compilent pas « sans Android » : il faut `androidx.annotation` et `errorprone annotations` au classpath, ou retirer les annotations (fichiers modifiés : Apache-2.0 §4(b)). Le **cœur de `DynamicScheme.java`** (constructeurs, `errorPalette`) n'en importe aucun, mais il dépend de `ColorSpecs` et `DynamicColor`, qui en importent. |
| E4 | `MaterialDynamicColors` fige `ColorSpec2026()` : `private val colorSpec: ColorSpec = ColorSpec2026()` (`MaterialDynamicColors.kt` L20 ; Java : `private static final ColorSpec colorSpec = new ColorSpec2026();` L28). `DynamicScheme.kt` l'instancie sans argument (L125). Le choix de la version se fait ensuite par rôle, à l'évaluation (`DynamicColor.getHct/getTone` → `ColorSpecs.get(scheme.specVersion)`, `DynamicColor.kt` L118 et L130 ; Java L311 et L323 ; `extendSpecVersion` Kotlin L227-275, comparaison `scheme.specVersion >= specVersion`). | ADR 0002 (suppose « un schéma 2021 ») ↔ code | Conséquence pour les `*Dim` : voir ci-dessous. |
| E5 | « Courbe `(1.0, 1.0, 3.0, 4.5)` contre `surfaceDim` » : exact pour les **conteneurs de rôle** (`primaryContainer`, `errorContainer`, fond = `highestSurface` = `surfaceDim` en clair, `surfaceBright` en sombre). Les `surfaceContainer*` ont, eux, leurs propres tons par courbe de contraste, sans fond. | ADR 0002 ↔ `ColorSpec2021.kt` | Lever l'ambiguïté du mot « conteneurs » dans l'ADR. |

## 1. Material Color Utilities

**Pourquoi :** épingler un commit et savoir quoi compiler (ADR 0001) ; savoir si le mécanisme
`errorPalette` est accessible (ADR 0002).
**Qui / dans quel but :** Google (Material Eng), dépôt `material-foundation/material-color-utilities`.
**Où :** https://github.com/material-foundation/material-color-utilities

### 1.1 Commit épinglé

| Champ | Valeur |
|---|---|
| Commit retenu par Pascal (ADR 0001, commit `a9da41d`) | `bd811e4ca2ab94c4ce3cadc89c19b1820b9704b3` (2026-10-19, « Automated Code Change », PiperOrigin-RevId 1002522783) |
| Ce qu'il change | `kotlin/dynamiccolor/DynamicScheme.kt` (+2 −2) et `kotlin/hct/ViewingConditions.kt` (+1 −1), d'après l'API GitHub `commits/<sha>` |
| Dernier commit de fond | `dd62982dfdefdfc9232ce8d76e122fb1c6d188ae` (2026-10-15, « Update Watch dynamic colors and SchemeContent across Java, Kotlin, and TypeScript ») |
| Précédents | `5b3618b16fdc…` (2026-08-21), `f05459ea2170…` (2026-08-10), `0f02217bffdb…` (2026-08-10) |
| Branche | `main`, tête au 2026-10-23 |
| Taille | 43 fichiers `.kt` sous `kotlin/`, 46 `.java` sous `java/` |
| Releases / tags | non consultés ; le README n'annonce pas d'artefact JVM (« Library availability » : Java renvoie vers `Color.md` de MDC-Android, Kotlin « available » sans lieu) |

`bd811e4` est la tête ; son seul écart avec `dd62982` (dernier commit de fond) est un
changement automatique de 3 lignes dans `kotlin/`. **Pascal a retenu `bd811e4`** (ADR 0001, commit `a9da41d`).
Les constats ci-dessous ont tous été lus à ce commit.

URL type : `https://raw.githubusercontent.com/material-foundation/material-color-utilities/bd811e4ca2ab94c4ce3cadc89c19b1820b9704b3/<chemin>`

### 1.2 Sources Kotlin : imports et version de Kotlin

- Aucun fichier de build sous `kotlin/` (ni Gradle ni Maven) ; seul fichier non `.kt` : `kotlin/LICENSE`.
  **Aucune version de Kotlin n'est publiée par MCU.**
- Imports lus sur **les 43 fichiers `.kt`** (grep `^import ` sur `kotlin/`, copie locale au commit `bd811e4`).
  Résultat : uniquement des paquets internes (`hct`, `utils`, `palettes`, `dynamiccolor`, `blend`, `quantize`,
  `scheme`, `score`, `dislike`, `temperature`, `contrast`, sans préfixe de domaine), `java.text.DecimalFormat`,
  `java.util.{ArrayList, Arrays, Collections, HashMap, LinkedHashMap, Locale, Random}` et `kotlin.math.*`.
  **Ni `android*`, ni `com.google.*`, ni aucune dépendance Maven.** La réserve « 26 fichiers non lus » tombe ;
  reste la **compilation** avec Kotlin 2.2 (mission 006).
- Paquets sans préfixe de domaine (`package dynamiccolor`, `package hct`…) : risque de collision de noms de paquets avec les
  applications, à noter pour le `dev`.
- Indices de version minimale vus dans les fichiers lus : virgules finales (Kotlin ≥ 1.4),
  `lowercase(Locale)` (≥ 1.5), `kotlin.math.cbrt` dans `ViewingConditions.kt` (introduit en 1.8, d'après mes connaissances,
  **non sourcé ici**). Aucune syntaxe postérieure à 1.9 repérée dans `DynamicScheme.kt` et
  `TonalPalette.kt`. **Compatibilité Kotlin 2.2 : non établie par compilation.**
- Dépendances JDK : `java.text.DecimalFormat`, `java.util.Locale`, `Random`, `LinkedHashMap` : JVM pur, compatible
  avec la règle « sans Android ».

### 1.3 Sources Java : imports (pour l'option 2 de l'ADR 0001)

Voir écart E3 : **huit** fichiers Java importent `androidx.annotation` ou `com.google.errorprone.annotations`
(`ColorSpec`, `ColorSpec2021` dont `errorprone.annotations.Var`, `ColorSpec2025`, `ColorSpec2026`, `DynamicColor`,
`MaterialDynamicColors`, `ToneDeltaPair`, `scheme/Scheme`). Aucun import `android.*` ni `javax.*` (grep sur `java/`, 0 occurrence).
Le code utilise une expression `switch` à flèche (`ColorSpec2021.java` L1457) : fait lu ; la version minimale de JDK qui en découle (14, d'après mes connaissances, **non sourcé ici**) est à confirmer par compilation.

### 1.4 Parité Java / Kotlin (lecture seulement)

Concordants à la lecture : `maybeFallbackSpecVersion` (mêmes cas), valeurs `surfaceDim`
(`6.0` sombre ; `ContrastCurve(87, 87, 80, 75)` clair), `surfaceContainerLow` et `surfaceContainerHighest`
(mêmes courbes), `error` (`errorPalette`, tons 80/40, `ContrastCurve(3, 4.5, 7, 7)`, fond `highestSurface`),
`primaryDim`/`errorDim` nuls dans `ColorSpec2021`, `harmonize` (rotation = min(écart × 0,5 ; 15°), Kotlin `Blend.kt` L37-47, rotation L41).
Divergence de forme : E2. **La parité des ARGB produits n'est pas mesurée** (mission 006).

### 1.5 Constructeur de `DynamicScheme` portant `errorPalette` : visibilité

- **Kotlin** (`kotlin/dynamiccolor/DynamicScheme.kt`) : `open class DynamicScheme(` L31, constructeur primaire
  **public** (pas de modificateur) : `sourceColorHctList: List<Hct>, variant, isDark, contrastLevel: Double,
  platform = DEFAULT_PLATFORM, specVersion = DEFAULT_SPEC_VERSION, primaryPalette, secondaryPalette, tertiaryPalette,
  neutralPalette, neutralVariantPalette, errorPalette: TonalPalette` (L33-52 ; `val errorPalette` public, sans défaut, L52). Constructeur secondaire
  **public** (L56-82) avec `sourceColorHct: Hct`. `DEFAULT_SPEC_VERSION = SPEC_2021`, `DEFAULT_PLATFORM = PHONE` (L290-291).
  `TonalPalette` : constructeur privé, fabriques publiques `fromHueAndChroma(hue, chroma)` (L172) et `fromHct(hct)` (L160) ; pas de `fromList`.
- **Java** (`DynamicScheme.java`) : quatre constructeurs **publics** (L82, L105, L131, L159) ; les trois derniers prennent
  `Optional<TonalPalette> errorPalette` ; champ `public final TonalPalette errorPalette` (L80).
- `Variant.kt` : `MONOCHROME, NEUTRAL, TONAL_SPOT, VIBRANT, EXPRESSIVE, FIDELITY, CONTENT, RAINBOW, FRUIT_SALAD, CMF`.
- `SchemeFidelity.kt` L41-96 appelle ce même constructeur public avec `errorPalette = ColorSpecs.get(specVersion).getErrorPalette(...)` (L88-95).
  **Le mécanisme de l'ADR 0002 (schéma construit avec la palette de « réussi » en `errorPalette`) est donc accessible, en Kotlin comme en Java.**
  Le `specVersion` passé est de toute façon réécrit par `maybeFallbackSpecVersion` (init L86-89, appel L88).

### 1.6 `errorPalette` et `error()` en `SPEC_2021`

`ColorSpec2021.java` L659-670 : `error()` = `.setPalette((s) -> s.errorPalette)` (L662), ton `isDark ? 80.0 : 40.0`, courbe
`ContrastCurve(3.0, 4.5, 7.0, 7.0)`, fond `highestSurface`, `ToneDeltaPair(errorContainer(), error(), 10.0, TonePolarity.NEARER, false)`.
`ColorSpec2021.kt` L603-622 : même définition (palette `scheme.errorPalette`, L607) ; forme différente du `ToneDeltaPair` en Kotlin
(`polarity = RELATIVE_LIGHTER`, `constraint = NEARER`) : divergence de forme, parité à mesurer en mission 006. `onError` (L627-635), `errorContainer` (L637-656),
`onErrorContainer` (L658-672) utilisent aussi `errorPalette` (courbes `(4.5, 7, 11, 21)`, `(1, 1, 3, 4.5)`, `(3, 4.5, 7, 11)`).
`DynamicScheme.kt` L241-251 : `error`, `onError`, `errorContainer`, `onErrorContainer` → `getArgb(dynamicColors.error…)`.
`ContrastCurve.get` : dès `contrastLevel ≥ 1,0` renvoie `high` (Kotlin `ContrastCurve.kt` L39-47, branche `else -> high` L45 ; Java `ContrastCurve.java` L62-72, dernière branche `return this.high`) ;
sous −1,0 ou à −1,0 : `low` ; entre : interpolation linéaire par paliers (−1→0 : low→normal ; 0→0,5 : normal→medium ; 0,5→1 : medium→high).

### 1.7 Comportement des `*Dim` dans un schéma 2021

- `ColorSpec2021` : `primaryDim`, `secondaryDim`, `tertiaryDim`, `errorDim` valent **`null`** (`DynamicColor?` en Kotlin).
  Kotlin `ColorSpec2021.kt` : `primaryDim` L331-332, `secondaryDim` L431, `tertiaryDim` L533, `errorDim` L624-625. Java `ColorSpec2021.java` : `primaryDim()` L377-379, `secondaryDim()` L480, `tertiaryDim()` L583, `errorDim()` L674-676 (`return null;`, annoté `@Nullable`).
  Ne lèvent pas d'exception.
- `surfaceDim` (Kotlin L115 ; Java L142) et `primaryFixedDim`/`secondaryFixedDim`/`tertiaryFixedDim` sont définis (non nuls) en 2021.
- `ColorSpec2025` (Kotlin `open class ColorSpec2025 : ColorSpec2021()`, L29 ; Java `class ColorSpec2025 extends ColorSpec2021`, L44) définit, Kotlin, `primaryDim` (L649), `secondaryDim` (L871), `tertiaryDim` (L1063) et `errorDim` (L1258-1277) **sans** `extendSpecVersion` ni `super` :
  des `DynamicColor` neufs, non nuls, avec fond `surfaceContainerHigh` (`errorDim` : L1265). Java : `primaryDim()` L664, `errorDim()` L1168 (même construction à neuf, non relue ligne à ligne). `ColorSpec2026` (Kotlin L304-309 pour `primaryDim`, L749-753 pour `errorDim`) les « remappe » sur `primary`/`error` via `extendSpecVersion` (Java L473-476, L949-952).
- `DynamicScheme.kt` n'expose **aucun** getter `primaryDim`/`secondaryDim`/`tertiaryDim`/`errorDim` (seulement `surfaceDim`, L151, et `XFixedDim`, L256/L268/L280). `MaterialDynamicColors.kt` les expose : `primaryDim` L114, `secondaryDim` L150, `tertiaryDim` L183, `errorDim` L216, typés `DynamicColor?`, via `colorSpec` figé sur `ColorSpec2026` (L20).
- Conséquence lue (à **confirmer par exécution**) : `MaterialDynamicColors().primaryDim` renvoie, via `ColorSpec2026`, un `DynamicColor` non nul
  même si le schéma est `SPEC_2021`, et son évaluation s'appuie sur la définition 2025 (non gardée par `extendSpecVersion`). Les `*Dim` de primaire, secondaire, tertiaire et
  erreur ne sont donc **pas** un rôle 2021 fiable ; `ColorScheme` de Compose ne les a pas non plus (§2). Seuls `surfaceDim` et `*FixedDim` sont communs.

### 1.8 Licence : Apache-2.0 et obligations du §4

- `LICENSE` à la racine, et `kotlin/LICENSE` : texte Apache License 2.0, « Copyright 2025 Google LLC » dans l'annexe (lu par WebFetch, résumé :
  « aucune altération évidente », non comparé caractère par caractère).
- **Pas de fichier NOTICE** : racine listée par ordre alphabétique (`.gitignore, CONTRIBUTING.md, LICENSE, README.md, cheat_sheet.png, concepts, cpp, dart, dev_guide, java, kotlin, swift` ; la liste est coupée après `swift`, `typescript` suit) ; aucun `NOTICE` sous `kotlin/`.
- §4 (https://www.apache.org/licenses/LICENSE-2.0.txt) : (a) remettre une copie de la licence ; (b) marquer en évidence les fichiers modifiés ;
  (c) conserver en code source les mentions de droit d'auteur, brevet, marque et attribution ; (d) reprendre le NOTICE s'il existe (ici : sans objet).
- GPL-3.0 : la liste de la FSF dit de la licence Apache 2.0 « compatible with version 3 of the GNU GPL » et incompatible avec la GPL 2
  (https://www.gnu.org/licenses/license-list.html#apache2). Ce qui est publié par `terrain-style` est du code Kotlin **généré** (jetons) :
  si les sources MCU ne sont pas redistribuées dans l'artefact, le §4 ne vise que le dossier `generation/mcu/`. **Avis juridique : aucun ; à Pascal.**

## 2. Compose Material 3 — `ColorScheme`

**Pourquoi :** savoir quels rôles existent pour fixer les contrastes à tester et les rôles « interdits en texte » (spec, besoin 2).
**Qui :** Google / AndroidX. **Où :** `androidx.compose.material3.ColorScheme`.
**Quand :** versions (https://developer.android.com/jetpack/androidx/releases/compose-material3) : stable **1.4.0 (2025-09-24)** ; bêta **1.5.0-beta02 (2026-10-21)** ; dernière alpha notée 1.5.0-alpha29 (2026-09-23).

### 2.1 Sources, comparées

| Source | Lecture | Résultat |
|---|---|---|
| C. **Jar de sources daté, Compose Material 3 1.4.0** : `https://dl.google.com/android/maven2/androidx/compose/material3/material3-android/1.4.0/material3-android-1.4.0-sources.jar` (sha256 `3e3209422a382566a780e2c1410edd866e4474498098fdbcf942c690ade1ddaa`, vérifié deux fois par l'orchestrateur et le relecteur) | jar décompressé ; `commonMain/androidx/compose/material3/ColorScheme.kt` lu par l'acquéreur (Read/Grep) : `class ColorScheme(` L148, 48 `val … : Color` L149-196, **sans `public`** (`grep -c 'public val'` = 0) | **48 rôles, même ordre que A et B (vérifié par l'acquéreur).** |
| C'. POM de la même version : `…/material3-android/1.4.0/material3-android-1.4.0.pom` | lu par WebFetch | version `1.4.0` ; dépendance `org.jetbrains.kotlin:kotlin-stdlib` **2.0.21** (gestion des dépendances et dépendance `runtime`). Fait utile à la question « Kotlin 2.2 » : Compose 1.4.0 est compilé contre le stdlib 2.0.21. |
| A. GitHub `androidx/androidx`, branche `androidx-main`, `compose/material3/material3/src/commonMain/kotlin/androidx/compose/material3/ColorScheme.kt` | constructeur primaire L149-198, `public class ColorScheme(` | 48 paramètres `public val … : Color` (tête `androidx-main` : modificateur `public` explicite, absent en 1.4.0, voir ci-dessous) |
| B. AOSP `android.googlesource.com`, branche `refs/heads/androidx-compose-material3-release`, même chemin, blob `eec269a5d4ffc8755d32553b94e33be3e33ce276` | constructeur primaire | 48 paramètres, mêmes noms |

**Concordance : A = B = C (48 rôles, mêmes noms, même ordre), lues par l'acquéreur.** Les sources A et B (têtes de branche au 2026-10-23)
ne sont pas rattachées à un numéro de version. **Version datée de la liste : Compose Material 3 1.4.0 (stable, 2025-09-24), source C** ;
A et B, plus récentes, donnent la même liste : aucun rôle n'a été ajouté ou retiré entre 1.4.0 et la tête de branche.
**Différence de forme (pas de fond) :** en 1.4.0 les 48 `val` n'ont pas de modificateur `public` ; la tête `androidx-main` écrit `public val`.
Le tag Git exact de 1.4.0 reste introuvable (404 sur `android.googlesource.com/…/refs/tags/androidx-compose-material3-release-1.4.0`) ; le jar Maven en tient lieu.
La page de référence developer.android.com/reference/…/ColorScheme n'a pu être lue (troncature) ; elle reste à consulter si Pascal veut une troisième source.

### 2.2 Les 48 rôles (ordre du constructeur)

| Groupe | Rôles |
|---|---|
| Primaire (5) | `primary`, `onPrimary`, `primaryContainer`, `onPrimaryContainer`, `inversePrimary` |
| Secondaire (4) | `secondary`, `onSecondary`, `secondaryContainer`, `onSecondaryContainer` |
| Tertiaire (4) | `tertiary`, `onTertiary`, `tertiaryContainer`, `onTertiaryContainer` |
| Fond et surface (9) | `background`, `onBackground`, `surface`, `onSurface`, `surfaceVariant`, `onSurfaceVariant`, `surfaceTint`, `inverseSurface`, `inverseOnSurface` |
| Erreur (4) | `error`, `onError`, `errorContainer`, `onErrorContainer` |
| Contour et voile (3) | `outline`, `outlineVariant`, `scrim` |
| Surfaces (7) | `surfaceBright`, `surfaceDim`, `surfaceContainer`, `surfaceContainerHigh`, `surfaceContainerHighest`, `surfaceContainerLow`, `surfaceContainerLowest` |
| Fixes (12) | `primaryFixed`, `primaryFixedDim`, `onPrimaryFixed`, `onPrimaryFixedVariant`, `secondaryFixed`, `secondaryFixedDim`, `onSecondaryFixed`, `onSecondaryFixedVariant`, `tertiaryFixed`, `tertiaryFixedDim`, `onTertiaryFixed`, `onTertiaryFixedVariant` |

Notes : aucun paramètre du constructeur primaire n'est déprécié. Deux constructeurs secondaires (sans rôles « fixes » ; sans `surfaceContainer*`) sont dépréciés
(notes 1.4.0-alpha15 : « constructor without Fixed color roles is now deprecated » ; « without surface container roles … marked hidden »).
`ColorScheme` n'a **ni `primaryDim`, ni `secondaryDim`, ni `tertiaryDim`, ni `errorDim`**. Anecdote relevée : `toString()` écrit `onPrimaryFixed=$onPrimaryContainer` (bogue amont, sans effet sur nos jetons).
Date d'introduction de `surfaceDim`/`surfaceContainer*`/fixes : non établie (notes 1.1 à 1.3 non lues).

## Trois points au sol (à vérifier par Pascal)

1. **Les 48 rôles de `ColorScheme`** : décompresser `material3-android-1.4.0-sources.jar` (`unzip -p … '*/ColorScheme.kt'`) ou ouvrir `ColorScheme.kt` dans Android Studio sur la version de Compose Material 3 que visent Marculus, Groma et Surculus, et compter 48 `val … : Color` dans `class ColorScheme(` (L148-196 en 1.4.0, sans `public` ; la tête `androidx-main` écrit `public val`) ; tout écart ⇒ la liste de la spec change.
2. **Le `E1`, `FIDELITY` ⇒ `SPEC_2021`** (la phrase de l'ADR 0001 est à corriger) : à `bd811e4`, lire `DynamicScheme.kt` L399-417 et vérifier que `FIDELITY` retombe à `SPEC_2021` quel que soit le `specVersion` demandé ; `CMF` garde la version demandée.
3. **Les `*Dim` en schéma 2021** (la lecture suppose un comportement que seule l'exécution confirme, écart E4) : avec le script de la mission 006, construire un `DynamicScheme` `FIDELITY` et lire `MaterialDynamicColors().primaryDim` ; non nul et évalué ⇒ la lecture de §1.7 tient ; nul ou exception ⇒ elle est fausse.

## Question de contrôle

Quelle décision cette donnée va-t-elle éclairer ? **Le choix, par Pascal, de l'option 1 de l'ADR 0001 (sources Kotlin recopiées à un commit inscrit) et celui de la méthode « réussi par `errorPalette` » de l'ADR 0002.**

## Journal proposé (à l'orchestrateur)

`2026-10-23 — mission 001 (acquereur) : faits MCU/Compose établis dans docs/referentiels/mcu-compose.md ; constructeur DynamicScheme(errorPalette) public en Kotlin et Java ; commit bd811e4 retenu par Pascal (ADR 0001) ; 5 écarts notés (E1-E5) dont MaterialDynamicColors figé sur ColorSpec2026 ; ColorScheme = 48 rôles, vérifiés dans le jar 1.4.0 décompressé (sans `public`, différent de la tête androidx-main) ; aller-retour 1 : lignes et imports revérifiés, 8 fichiers Java à annotations ; aller-retour 2 : forme corrigée ; parité Java/Kotlin à mesurer (mission 006).`
