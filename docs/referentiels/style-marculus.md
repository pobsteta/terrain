# Référentiel — le style de Marculus (v0.65.0) et Compose Material 3 (BOM 2026.02.01)

- **Propriétaire :** Pascal Obstétar
- **Relevé par :** `dev`, mission 020 (`pilotage/missions/020-referentiel-style-marculus.md`)
- **Date de relevé :** 2026-10-24
- **Décision éclairée :** ADR 0006 (« `terrain-style` reprend le style de Marculus »). Ce fichier
  **relève** ; il ne tranche rien.
- **Statut :** relevé le 2026-10-24 ; **relu** par le `relecteur` le 2026-10-24 (96 couleurs, `Shapes`, `Typography`, boutons et écart HCT rejoués indépendamment, 0 écart) ; liste de contrôle passée ; non encore validé par Pascal (nuancier de la mission 022).
- **Mission redite :** relever dans Marculus (tag v0.65.0) et dans Compose Material 3 (BOM
  `2026.02.01`) les 48 rôles de couleur en clair et en sombre, les `Shapes`, la `Typography` et
  les dimensions et formes des boutons, puis mesurer l'écart de teinte HCT entre `reussi` et le
  `primary` de Marculus, pour que `terrain-style` recopie le style sans rien deviner.
- **Table machine :** `terrain-style/src/test/resources/jetons/style-marculus.csv`
  (`theme,role,argb` ; 96 lignes de couleurs, 48 par thème ; `argb` en `0xFFRRGGBB`).

## Sources et versions

| Quoi | Version | Comment on y accède | Empreinte |
|---|---|---|---|
| Marculus | tag `v0.65.0` = commit `c73c9a0ce3bb839f80f22b93354c0b4a95d5ed91` (le tag existe) | `git -C /home/pascal/dev/marculus show v0.65.0:<chemin>` ; arbre `app/src/main` extrait par `git archive v0.65.0 app/src/main` pour les comptages | n.a. |
| BOM Compose | `androidx.compose:compose-bom:2026.02.01` (`gradle/libs.versions.toml` de Marculus **L5** : `composeBom = "2026.02.01"` ; `app/build.gradle.kts` L87 `platform(libs.compose.bom)`, L91 `libs.compose.material3` sans version) | `https://dl.google.com/android/maven2/androidx/compose/compose-bom/2026.02.01/compose-bom-2026.02.01.pom` | sha256 `4f3c7a3f98a2e7d1104a37eb5b6fb7ee556239af69a6d157c1cbd85651d538b4` |
| Material 3 | **1.4.0** (le pom du BOM donne `androidx.compose.material3:material3` et `material3-android` en `1.4.0`) | jar `…/material3-android/1.4.0/material3-android-1.4.0-sources.jar` (Google Maven) ; fichiers sous `commonMain/androidx/compose/material3/` | sha256 `3e3209422a382566a780e2c1410edd866e4474498098fdbcf942c690ade1ddaa` (même jar que `docs/referentiels/mcu-compose.md`) |
| Compose UI Text | 1.10.4 (le pom du BOM) | `…/ui-text-android/1.10.4/ui-text-android-1.10.4-sources.jar`, `FontWeight.kt` | sha256 `9440b0cae0c4d7f811d840bde40c3eeeb466d53a78a266932fe5f4b3ce6503a5` |
| Compose Foundation | 1.10.4 (le pom du BOM) | `…/foundation-android/1.10.4/foundation-android-1.10.4-sources.jar`, `RoundedCornerShape.kt` | sha256 `b918e13fb35df24fcfcdcbbe8cb4115cb41e97c854151d55af0ef92317d642a9` |

Le cache Gradle local (`~/.gradle/caches`) ne contient pas le BOM `2026.02.01` (il a `2026.09.00`,
`2024.12.01`, et `material3-android` 1.3.1 et 1.4.0) : le pom du BOM `2026.02.01` a donc été
téléchargé, et seul le jar `-sources` 1.4.0 a servi pour les valeurs. Aucun `javap` n'a été
nécessaire (les sources existent). Les numéros de ligne ci-dessous sont ceux de ces fichiers
décompressés ; ils changent si la version change. **Non vérifié :** la version réellement
résolue par une compilation de Marculus (`./gradlew dependencies`), qui pourrait être montée par
une dépendance transitive ; la lecture du `libs.versions.toml` et de `build.gradle.kts` ne
l'exclut pas, elle l'indique seulement.

## Écarts à lire d'abord

| # | Écart | Entre quoi et quoi | Suite |
|---|---|---|---|
| E1 | **`surfaceTint` n'est pas « par défaut ».** Dans `lightColorScheme()` et `darkColorScheme()`, le paramètre est `surfaceTint: Color = primary` (`ColorScheme.kt` L864 et L966) : avec le `primary` de Marculus, `surfaceTint` vaut `#3E7A40` dans les deux thèmes, et non `#6750A4` (clair) ou `#D0BCFF` (sombre). Marculus fixe donc **5 rôles** dans `Theme.kt`, mais **6 rôles** diffèrent des jetons par défaut. | `ColorScheme.kt` | **Levé le 2026-10-24** : l'ADR 0006 est alignée (42 rôles par défaut, `surfaceTint` suit `primary`). Le CSV porte `#3E7A40` pour `surfaceTint`. |
| E2 | **`reussi.couleur` n'est pas `#0F8558`.** `#0F8558` est la teinte de départ (graine) ; les valeurs publiées sont `couleur` = `#00341F` (clair) et `#BBFFD7` (sombre) (`StyleTerrain.kt` L72 et L126, `origin/main` au moment du relevé). L'écart de teinte est mesuré sur ces `couleur`, et aussi sur la graine, pour information. | `StyleTerrain.kt` | **Levé le 2026-10-24** : l'ADR 0006 est alignée (graine `#0F8558`, `couleur` `#00341F` / `#BBFFD7`). |
| E3 | **Les 42 rôles par défaut sont la palette de base de Material 3, violette et rose.** Exemples (clair) : `inversePrimary` `#D0BCFF`, `secondaryContainer` `#E8DEF8`, `tertiary` `#7D5260`, `tertiaryContainer` `#FFD8E4`, `primaryFixed` `#EADDFF`, `background` et `surface` `#FEF7FF`. Marculus n'en montre que ce qu'il affiche (surfaces, texte, erreur) ; les rôles `tertiary*`, `*Fixed*` et `inversePrimary` n'ont pas de vert. | ADR 0006 ↔ `ColorLightTokens.kt` / `ColorDarkTokens.kt` | À dire à Pascal avant d'écrire le nuancier : c'est la conséquence de « le reste vaut le défaut de Material 3 ». |
| E4 | **Les boutons de Material 3 1.4.0 sont des pilules.** `ButtonDefaults.shape`, `outlinedShape` et `textShape` valent `ButtonSmallTokens.ContainerShapeRound` = `CornerFull` = `CircleShape` = `RoundedCornerShape(50)`, pas un rayon en dp. Le rayon en dp n'existe pas pour un bouton ; `terrain-style` devra exposer « 50 % ». | spec (« rayon … en dp ») ↔ `Button.kt` L550-567 | À dire à Pascal ; le spec dit « rayon en dp » pour les boutons. |
| E5 | **`Shapes` et `Typography` de la 1.4.0 ont plus de membres que le décompte des 5 et des 15.** `Shapes` : 5 publics (`extraSmall` … `extraLarge`) + 3 `internal` (`largeIncreased` 20 dp, `extraLargeIncreased` 32 dp, `extraExtraLarge` 48 dp) + `None` (`RectangleShape`) et `Full` (`CircleShape`) qui ne sont pas des membres. `Typography` : 15 publics + 15 `*Emphasized` `internal`. Seuls les publics sont relevés en entier ; les `internal` sont listés pour mémoire. | spec (5 et 15) ↔ `Shapes.kt` L76-83, `Typography.kt` L89-118 | Aucune ; les décomptes de la mission (5 et 15) sont ceux des membres publics. |
| E6 | **Marculus ne fixe ni `Shapes`, ni `Typography`, ni dimensions de bouton au niveau du thème.** `MarculusTheme` passe seulement `colorScheme` à `MaterialTheme` (`Theme.kt` L35-38). Tout vient des défauts M3 1.4.0, sauf des cas ponctuels, dans les écrans (voir « Usages »). Le « style de Marculus » pour les formes, la typographie et les boutons est donc **le défaut M3 1.4.0**. | ADR 0006 (« ce que Marculus fixe, plus les valeurs par défaut ») ↔ `Theme.kt` | À dire à Pascal : la partie « Marculus » de ces trois sections est vide au niveau du thème. |
| E7 | **Le thème XML de Marculus n'est pas Material 3.** `res/values/themes.xml` : `Theme.Marculus` hérite de `@android:style/Theme.Material.Light.NoActionBar` (nuit : `Theme.Material.NoActionBar`) ; il ne définit aucune couleur. Il ne sert qu'avant Compose (fenêtre de démarrage). | spec (« thème XML ») | À noter ; aucune valeur à recopier. |

## 1. Couleurs : 48 rôles × 2 thèmes

**Source de Marculus** : `app/src/main/kotlin/io/github/pobsteta/marculus/ui/theme/Theme.kt` au tag
`v0.65.0` :

- L10 `VertMarculus = Color(0xFF374742)` ; L11 `VertVif = Color(0xFF3E7A40)` ;
  L12 `Creme = Color(0xFFF5EBD8)` ;
- L14-20 `LightColors = lightColorScheme(primary = VertVif, onPrimary = Color.White,
  primaryContainer = VertMarculus, onPrimaryContainer = Creme, secondary = VertMarculus)` ;
- L22-28 `DarkColors = darkColorScheme(primary = VertVif, onPrimary = Color.White,
  primaryContainer = VertMarculus, onPrimaryContainer = Creme, secondary = Creme)` ;
- L30-39 `MarculusTheme(darkTheme = isSystemInDarkTheme())` choisit l'un ou l'autre.

**Source de Material 3** (1.4.0) : `ColorLightTokens.kt` (48 jetons, L22-69), `ColorDarkTokens.kt`
(48 jetons, L22-69), `PaletteTokens.kt` (valeurs `Color(red, green, blue)`), et les fonctions
`lightColorScheme()` (`ColorScheme.kt` L844, avec les rôles `*Fixed`) et `darkColorScheme()`
(`ColorScheme.kt` L946). Les surcharges plus anciennes (L1153 et suivantes) sont
`@Deprecated(level = HIDDEN)` : l'appel de Marculus (cinq paramètres nommés) résout vers L844 et
L946.

### Les cinq rôles fixés par Marculus, et `surfaceTint`

| Rôle | Clair | Sombre | Source Marculus | Défaut M3 clair / sombre |
|---|---|---|---|---|
| `primary` | `#3E7A40` (`VertVif`) | `#3E7A40` | `Theme.kt` L15 / L23 | `#6750A4` / `#D0BCFF` |
| `onPrimary` | `#FFFFFF` (`Color.White`) | `#FFFFFF` | L16 / L24 | `#FFFFFF` / `#381E72` |
| `primaryContainer` | `#374742` (`VertMarculus`) | `#374742` | L17 / L25 | `#EADDFF` / `#4F378B` |
| `onPrimaryContainer` | `#F5EBD8` (`Creme`) | `#F5EBD8` | L18 / L26 | `#21005D` / `#EADDFF` |
| `secondary` | `#374742` (`VertMarculus`) | `#F5EBD8` (`Creme`) | L19 / L27 | `#625B71` / `#CCC2DC` |
| `surfaceTint` (non fixé ; suit `primary`) | `#3E7A40` | `#3E7A40` | `ColorScheme.kt` L864 / L966 | `#6750A4` / `#D0BCFF` |

### Les 48 rôles

Ordre des paramètres de `lightColorScheme()` (`ColorScheme.kt` L845-892). `Color.White` =
`#FFFFFF`. « Jeton » est la ligne du fichier de jetons, « palette » celle de `PaletteTokens.kt`.
Une valeur en gras vient de Marculus ; les autres sont les défauts de M3 1.4.0.

| Rôle | Clair | Source (clair) | Sombre | Source (sombre) |
|---|---|---|---|---|
| `primary` | #3E7A40 | **Marculus** `Theme.kt` L15 (défaut M3 : #6750A4, `ColorLightTokens` L47 → `PaletteTokens.Primary40` L80) | #3E7A40 | **Marculus** `Theme.kt` L23 (défaut M3 : #D0BCFF, `ColorDarkTokens` L47 → `PaletteTokens.Primary80` L84) |
| `onPrimary` | #FFFFFF | **Marculus** `Theme.kt` L16 (défaut M3 : #FFFFFF, `ColorLightTokens` L31 → `PaletteTokens.Primary100` L77) | #FFFFFF | **Marculus** `Theme.kt` L24 (défaut M3 : #381E72, `ColorDarkTokens` L31 → `PaletteTokens.Primary20` L78) |
| `primaryContainer` | #374742 | **Marculus** `Theme.kt` L17 (défaut M3 : #EADDFF, `ColorLightTokens` L48 → `PaletteTokens.Primary90` L85) | #374742 | **Marculus** `Theme.kt` L25 (défaut M3 : #4F378B, `ColorDarkTokens` L48 → `PaletteTokens.Primary30` L79) |
| `onPrimaryContainer` | #F5EBD8 | **Marculus** `Theme.kt` L18 (défaut M3 : #21005D, `ColorLightTokens` L32 → `PaletteTokens.Primary10` L76) | #F5EBD8 | **Marculus** `Theme.kt` L26 (défaut M3 : #EADDFF, `ColorDarkTokens` L32 → `PaletteTokens.Primary90` L85) |
| `inversePrimary` | #D0BCFF | `ColorLightTokens` L26 → `PaletteTokens.Primary80` L84 | #6750A4 | `ColorDarkTokens` L26 → `PaletteTokens.Primary40` L80 |
| `secondary` | #374742 | **Marculus** `Theme.kt` L19 (défaut M3 : #625B71, `ColorLightTokens` L52 → `PaletteTokens.Secondary40` L93) | #F5EBD8 | **Marculus** `Theme.kt` L27 (défaut M3 : #CCC2DC, `ColorDarkTokens` L52 → `PaletteTokens.Secondary80` L97) |
| `onSecondary` | #FFFFFF | `ColorLightTokens` L35 → `PaletteTokens.Secondary100` L90 | #332D41 | `ColorDarkTokens` L35 → `PaletteTokens.Secondary20` L91 |
| `secondaryContainer` | #E8DEF8 | `ColorLightTokens` L53 → `PaletteTokens.Secondary90` L98 | #4A4458 | `ColorDarkTokens` L53 → `PaletteTokens.Secondary30` L92 |
| `onSecondaryContainer` | #1D192B | `ColorLightTokens` L36 → `PaletteTokens.Secondary10` L89 | #E8DEF8 | `ColorDarkTokens` L36 → `PaletteTokens.Secondary90` L98 |
| `tertiary` | #7D5260 | `ColorLightTokens` L66 → `PaletteTokens.Tertiary40` L106 | #EFB8C8 | `ColorDarkTokens` L66 → `PaletteTokens.Tertiary80` L110 |
| `onTertiary` | #FFFFFF | `ColorLightTokens` L41 → `PaletteTokens.Tertiary100` L103 | #492532 | `ColorDarkTokens` L41 → `PaletteTokens.Tertiary20` L104 |
| `tertiaryContainer` | #FFD8E4 | `ColorLightTokens` L67 → `PaletteTokens.Tertiary90` L111 | #633B48 | `ColorDarkTokens` L67 → `PaletteTokens.Tertiary30` L105 |
| `onTertiaryContainer` | #31111D | `ColorLightTokens` L42 → `PaletteTokens.Tertiary10` L102 | #FFD8E4 | `ColorDarkTokens` L42 → `PaletteTokens.Tertiary90` L111 |
| `background` | #FEF7FF | `ColorLightTokens` L22 → `PaletteTokens.Neutral98` L60 | #141218 | `ColorDarkTokens` L22 → `PaletteTokens.Neutral6` L50 |
| `onBackground` | #1D1B20 | `ColorLightTokens` L28 → `PaletteTokens.Neutral10` L39 | #E6E0E9 | `ColorDarkTokens` L28 → `PaletteTokens.Neutral90` L55 |
| `surface` | #FEF7FF | `ColorLightTokens` L56 → `PaletteTokens.Neutral98` L60 | #141218 | `ColorDarkTokens` L56 → `PaletteTokens.Neutral6` L50 |
| `onSurface` | #1D1B20 | `ColorLightTokens` L39 → `PaletteTokens.Neutral10` L39 | #E6E0E9 | `ColorDarkTokens` L39 → `PaletteTokens.Neutral90` L55 |
| `surfaceVariant` | #E7E0EC | `ColorLightTokens` L65 → `PaletteTokens.NeutralVariant90` L72 | #49454F | `ColorDarkTokens` L65 → `PaletteTokens.NeutralVariant30` L66 |
| `onSurfaceVariant` | #49454F | `ColorLightTokens` L40 → `PaletteTokens.NeutralVariant30` L66 | #CAC4D0 | `ColorDarkTokens` L40 → `PaletteTokens.NeutralVariant80` L71 |
| `surfaceTint` | #3E7A40 | `surfaceTint = primary` (`ColorScheme.kt` L864 clair, L966 sombre) : suit `primary` de Marculus | #3E7A40 | `surfaceTint = primary` (`ColorScheme.kt` L864 clair, L966 sombre) : suit `primary` de Marculus |
| `inverseSurface` | #322F35 | `ColorLightTokens` L27 → `PaletteTokens.Neutral20` L43 | #E6E0E9 | `ColorDarkTokens` L27 → `PaletteTokens.Neutral90` L55 |
| `inverseOnSurface` | #F5EFF7 | `ColorLightTokens` L25 → `PaletteTokens.Neutral95` L58 | #322F35 | `ColorDarkTokens` L25 → `PaletteTokens.Neutral20` L43 |
| `error` | #B3261E | `ColorLightTokens` L23 → `PaletteTokens.Error40` L30 | #F2B8B5 | `ColorDarkTokens` L23 → `PaletteTokens.Error80` L34 |
| `onError` | #FFFFFF | `ColorLightTokens` L29 → `PaletteTokens.Error100` L27 | #601410 | `ColorDarkTokens` L29 → `PaletteTokens.Error20` L28 |
| `errorContainer` | #F9DEDC | `ColorLightTokens` L24 → `PaletteTokens.Error90` L35 | #8C1D18 | `ColorDarkTokens` L24 → `PaletteTokens.Error30` L29 |
| `onErrorContainer` | #410E0B | `ColorLightTokens` L30 → `PaletteTokens.Error10` L26 | #F9DEDC | `ColorDarkTokens` L30 → `PaletteTokens.Error90` L35 |
| `outline` | #79747E | `ColorLightTokens` L45 → `PaletteTokens.NeutralVariant50` L68 | #938F99 | `ColorDarkTokens` L45 → `PaletteTokens.NeutralVariant60` L69 |
| `outlineVariant` | #CAC4D0 | `ColorLightTokens` L46 → `PaletteTokens.NeutralVariant80` L71 | #49454F | `ColorDarkTokens` L46 → `PaletteTokens.NeutralVariant30` L66 |
| `scrim` | #000000 | `ColorLightTokens` L51 → `PaletteTokens.Neutral0` L38 | #000000 | `ColorDarkTokens` L51 → `PaletteTokens.Neutral0` L38 |
| `surfaceBright` | #FEF7FF | `ColorLightTokens` L57 → `PaletteTokens.Neutral98` L60 | #3B383E | `ColorDarkTokens` L57 → `PaletteTokens.Neutral24` L45 |
| `surfaceContainer` | #F3EDF7 | `ColorLightTokens` L58 → `PaletteTokens.Neutral94` L57 | #211F26 | `ColorDarkTokens` L58 → `PaletteTokens.Neutral12` L41 |
| `surfaceContainerHigh` | #ECE6F0 | `ColorLightTokens` L59 → `PaletteTokens.Neutral92` L56 | #2B2930 | `ColorDarkTokens` L59 → `PaletteTokens.Neutral17` L42 |
| `surfaceContainerHighest` | #E6E0E9 | `ColorLightTokens` L60 → `PaletteTokens.Neutral90` L55 | #36343B | `ColorDarkTokens` L60 → `PaletteTokens.Neutral22` L44 |
| `surfaceContainerLow` | #F7F2FA | `ColorLightTokens` L61 → `PaletteTokens.Neutral96` L59 | #1D1B20 | `ColorDarkTokens` L61 → `PaletteTokens.Neutral10` L39 |
| `surfaceContainerLowest` | #FFFFFF | `ColorLightTokens` L62 → `PaletteTokens.Neutral100` L40 | #0F0D13 | `ColorDarkTokens` L62 → `PaletteTokens.Neutral4` L47 |
| `surfaceDim` | #DED8E1 | `ColorLightTokens` L63 → `PaletteTokens.Neutral87` L54 | #141218 | `ColorDarkTokens` L63 → `PaletteTokens.Neutral6` L50 |
| `primaryFixed` | #EADDFF | `ColorLightTokens` L49 → `PaletteTokens.Primary90` L85 | #EADDFF | `ColorDarkTokens` L49 → `PaletteTokens.Primary90` L85 |
| `primaryFixedDim` | #D0BCFF | `ColorLightTokens` L50 → `PaletteTokens.Primary80` L84 | #D0BCFF | `ColorDarkTokens` L50 → `PaletteTokens.Primary80` L84 |
| `onPrimaryFixed` | #21005D | `ColorLightTokens` L33 → `PaletteTokens.Primary10` L76 | #21005D | `ColorDarkTokens` L33 → `PaletteTokens.Primary10` L76 |
| `onPrimaryFixedVariant` | #4F378B | `ColorLightTokens` L34 → `PaletteTokens.Primary30` L79 | #4F378B | `ColorDarkTokens` L34 → `PaletteTokens.Primary30` L79 |
| `secondaryFixed` | #E8DEF8 | `ColorLightTokens` L54 → `PaletteTokens.Secondary90` L98 | #E8DEF8 | `ColorDarkTokens` L54 → `PaletteTokens.Secondary90` L98 |
| `secondaryFixedDim` | #CCC2DC | `ColorLightTokens` L55 → `PaletteTokens.Secondary80` L97 | #CCC2DC | `ColorDarkTokens` L55 → `PaletteTokens.Secondary80` L97 |
| `onSecondaryFixed` | #1D192B | `ColorLightTokens` L37 → `PaletteTokens.Secondary10` L89 | #1D192B | `ColorDarkTokens` L37 → `PaletteTokens.Secondary10` L89 |
| `onSecondaryFixedVariant` | #4A4458 | `ColorLightTokens` L38 → `PaletteTokens.Secondary30` L92 | #4A4458 | `ColorDarkTokens` L38 → `PaletteTokens.Secondary30` L92 |
| `tertiaryFixed` | #FFD8E4 | `ColorLightTokens` L68 → `PaletteTokens.Tertiary90` L111 | #FFD8E4 | `ColorDarkTokens` L68 → `PaletteTokens.Tertiary90` L111 |
| `tertiaryFixedDim` | #EFB8C8 | `ColorLightTokens` L69 → `PaletteTokens.Tertiary80` L110 | #EFB8C8 | `ColorDarkTokens` L69 → `PaletteTokens.Tertiary80` L110 |
| `onTertiaryFixed` | #31111D | `ColorLightTokens` L43 → `PaletteTokens.Tertiary10` L102 | #31111D | `ColorDarkTokens` L43 → `PaletteTokens.Tertiary10` L102 |
| `onTertiaryFixedVariant` | #633B48 | `ColorLightTokens` L44 → `PaletteTokens.Tertiary30` L105 | #633B48 | `ColorDarkTokens` L44 → `PaletteTokens.Tertiary30` L105 |

**Décompte :** 48 rôles par thème (`ColorLightTokens.kt` et `ColorDarkTokens.kt`, L22-69 : 48
lignes `val`), 96 lignes dans le CSV. Contrôle : un second dépouillement indépendant du premier
(`split` au lieu d'une expression régulière) relit les 94 lignes `PaletteTokens.*` des deux
fichiers de jetons et les compare au CSV : 0 écart. Les 2 autres lignes sont `SurfaceTint = Primary`.
Les rôles hors des 48 : couleurs clés (`*PaletteKeyColor`), `*Dim` de SPEC_2025+, `controlActivated`
… ne sont pas des paramètres de `lightColorScheme()` 1.4.0 et ne sont pas relevés.

## 2. Formes : `Shapes`

**Défauts M3 1.4.0** (`Shapes.kt` L76-80 : `extraSmall = ShapeDefaults.ExtraSmall` … ;
`ShapeDefaults` L240-257 ; `ShapeTokens.kt`) :

| Forme (`Shapes`) | Rayon d'angle | Source |
|---|---|---|
| `extraSmall` | 4 dp | `ShapeDefaults.ExtraSmall` (`Shapes.kt` L242) → `ShapeTokens.CornerExtraSmall = RoundedCornerShape(4.0.dp)` (`ShapeTokens.kt` L38) |
| `small` | 8 dp | `Shapes.kt` L245 → `ShapeTokens.kt` L72 `RoundedCornerShape(8.0.dp)` |
| `medium` | 12 dp | `Shapes.kt` L248 → `ShapeTokens.kt` L70 `RoundedCornerShape(12.0.dp)` |
| `large` | 16 dp | `Shapes.kt` L251 → `ShapeTokens.kt` L47 `RoundedCornerShape(16.0.dp)` |
| `extraLarge` | 28 dp | `Shapes.kt` L257 → `ShapeTokens.kt` L29 `RoundedCornerShape(28.0.dp)` |

**Décompte : 5 formes publiques.** Pour mémoire (non publiques ou hors de `Shapes`) :
`largeIncreased` 20 dp (L55), `extraLargeIncreased` 32 dp (L30), `extraExtraLarge` 48 dp (L28) —
`internal` dans `Shapes.kt` L81-83 ; `CornerNone = RectangleShape` (L71) ; `CornerFull =
CircleShape` (L46), et `CircleShape = RoundedCornerShape(50)` (Compose Foundation 1.10.4,
`RoundedCornerShape.kt` L151).

**Marculus** : ne passe aucun `shapes =` à `MaterialTheme` (`Theme.kt` L35-38) et n'utilise pas
`MaterialTheme.shapes` (recherche `MaterialTheme\.shapes`, 0 occurrence). Une seule forme
explicite dans tout `app/src/main` : `RoundedCornerShape(50)` (pilule), `BadgeFix.kt` L104.

**Formes des composants que Marculus emploie** (M3 1.4.0) :

| Composant | Forme | Source |
|---|---|---|
| `Button`, `OutlinedButton`, `TextButton`, `ElevatedButton`, `FilledTonalButton` | pilule, `RoundedCornerShape(50)` | `Button.kt` L550-567 → `ButtonSmallTokens.kt` L25 `ContainerShapeRound = CornerFull` |
| `IconButton` (et variantes) | pilule | `IconButtonDefaults.kt` L849-858 → `SmallIconButtonTokens.kt` L25 |
| `FloatingActionButton` | `large`, 16 dp | `FloatingActionButton.kt` L513-514 → `FabBaselineTokens.kt` L25 `CornerLarge` |
| `SmallFloatingActionButton` | `medium`, 12 dp | `FloatingActionButton.kt` L517-518 → `FabSmallTokens.kt` L25 `CornerMedium` |
| `Card`, `ElevatedCard` | `medium`, 12 dp | `Card.kt` L370-375 → `FilledCardTokens.kt` L26, `ElevatedCardTokens.kt` L26 |
| `AssistChip`, `FilterChip` | `small`, 8 dp | `AssistChipTokens.kt` L25, `FilterChipTokens.kt` L25 |
| `AlertDialog` | `extraLarge`, 28 dp | `DialogTokens.kt` L31 |
| `OutlinedTextField` | `extraSmall`, 4 dp | `OutlinedTextFieldTokens.kt` L26 |

## 3. Typographie : `Typography`

**Défauts M3 1.4.0** : `Typography.kt` L89-103 (15 paramètres publics, défauts
`TypographyTokens.*`), repris par le constructeur public L220 et suivantes ; `TypographyTokens.kt`
(chaque style = `DefaultTextStyle.copy(fontFamily, fontWeight, fontSize, lineHeight,
letterSpacing)`) ; valeurs dans `TypeScaleTokens.kt` ; `TypefaceTokens.kt` L25-29 : `Brand` et
`Plain` = `FontFamily.SansSerif`, `WeightRegular` = `FontWeight.Normal`, `WeightMedium` =
`FontWeight.Medium`, `WeightBold` = `FontWeight.Bold` ; `FontWeight.Normal` = 400,
`Medium` = 500, `Bold` = 700 (Compose UI Text 1.10.4, `FontWeight.kt` L60, L62, L66). Commun aux
15 styles : `lineHeightStyle = LineHeightStyle(alignment = Center, trim = None)` (`TypographyTokens.kt`
L269-270), `platformStyle = defaultPlatformTextStyle()` (L274).

| Style | Taille | Interligne | Graisse | Interlettrage | Famille | Lignes de `TypeScaleTokens.kt` |
|---|---|---|---|---|---|---|
| `displayLarge` | 57 sp | 64 sp | 400 (Normal) | -0.2 sp | SansSerif (`Brand`) | L39-43 |
| `displayMedium` | 45 sp | 52 sp | 400 (Normal) | 0.0 sp | SansSerif (`Brand`) | L44-48 |
| `displaySmall` | 36 sp | 44 sp | 400 (Normal) | 0.0 sp | SansSerif (`Brand`) | L49-53 |
| `headlineLarge` | 32 sp | 40 sp | 400 (Normal) | 0.0 sp | SansSerif (`Brand`) | L54-58 |
| `headlineMedium` | 28 sp | 36 sp | 400 (Normal) | 0.0 sp | SansSerif (`Brand`) | L59-63 |
| `headlineSmall` | 24 sp | 32 sp | 400 (Normal) | 0.0 sp | SansSerif (`Brand`) | L64-68 |
| `titleLarge` | 22 sp | 28 sp | 400 (Normal) | 0.0 sp | SansSerif (`Brand`) | L84-88 |
| `titleMedium` | 16 sp | 24 sp | 500 (Medium) | 0.2 sp | SansSerif (`Plain`) | L89-93 |
| `titleSmall` | 14 sp | 20 sp | 500 (Medium) | 0.1 sp | SansSerif (`Plain`) | L94-98 |
| `bodyLarge` | 16 sp | 24 sp | 400 (Normal) | 0.5 sp | SansSerif (`Plain`) | L24-28 |
| `bodyMedium` | 14 sp | 20 sp | 400 (Normal) | 0.2 sp | SansSerif (`Plain`) | L29-33 |
| `bodySmall` | 12 sp | 16 sp | 400 (Normal) | 0.4 sp | SansSerif (`Plain`) | L34-38 |
| `labelLarge` | 14 sp | 20 sp | 500 (Medium) | 0.1 sp | SansSerif (`Plain`) | L69-73 |
| `labelMedium` | 12 sp | 16 sp | 500 (Medium) | 0.5 sp | SansSerif (`Plain`) | L74-78 |
| `labelSmall` | 11 sp | 16 sp | 500 (Medium) | 0.5 sp | SansSerif (`Plain`) | L79-83 |

**Décompte : 15 styles.** Pour mémoire : 15 styles `*Emphasized` (`TypeScaleTokens.kt` L100-174),
`internal`, non relevés.

**Marculus** : ne passe aucun `typography =` à `MaterialTheme` (`Theme.kt` L35-38), ne fixe aucune
famille de police. Il emploie 10 des 15 styles par `MaterialTheme.typography.*` (comptage sur
`app/src/main` au tag) : `bodySmall` 61, `titleSmall` 23, `bodyMedium` 18, `titleMedium` 16,
`labelSmall` 10, `bodyLarge` 6, `labelMedium` 4, `titleLarge` 3, `headlineMedium` 2,
`labelLarge` 2 ; non employés : `displayLarge`, `displayMedium`, `displaySmall`, `headlineLarge`,
`headlineSmall`. Les surcharges ponctuelles dans les écrans sont classées plus bas.

## 4. Boutons : dimensions et formes

**Défauts M3 1.4.0** (`Button.kt`, `ButtonDefaults` L462-) ; ce qu'emploie Marculus : `Button` 8,
`OutlinedButton` 17, `TextButton` 52, `IconButton` 16, `FloatingActionButton` 2,
`SmallFloatingActionButton` 4 (comptage de `\b<Nom>\(` sur `app/src/main` au tag ; `FilledTonalButton`,
`ElevatedButton`, `OutlinedIconButton` : 0).

| Dimension | Valeur | Source |
|---|---|---|
| Hauteur minimale (`Button`, `OutlinedButton`, `TextButton`, `ElevatedButton`, `FilledTonalButton`) | **40 dp** | `ButtonDefaults.MinHeight = ButtonSmallTokens.ContainerHeight` (`Button.kt` L533) → `ButtonSmallTokens.kt` L24 ; appliquée par `defaultMinSize` (`Button.kt` L144-147) |
| Largeur minimale | **58 dp** | `ButtonDefaults.MinWidth` (`Button.kt` L527) |
| Marges intérieures `Button`, `OutlinedButton`, `ElevatedButton`, `FilledTonalButton` (`ContentPadding`) | début **24**, haut **8**, fin **24**, bas **8** dp | `Button.kt` L479-485 ; `ButtonLeadingSpace`/`ButtonTrailingSpace` = `BaselineButtonTokens` L48 et L63 (24 dp) ; `ButtonVerticalPadding` L469 (8 dp) |
| Marges intérieures `Button` avec icône (`ButtonWithIconContentPadding`) | début **16**, haut **8**, fin **24**, bas **8** dp | `Button.kt` L466 (16 dp), L488-494 |
| Marges intérieures `TextButton` (`TextButtonContentPadding`) | début **12**, haut **8**, fin **12**, bas **8** dp | `Button.kt` L497 (12 dp), L504-510 |
| Marges intérieures `TextButton` avec icône (`TextButtonWithIconContentPadding`) | début **12**, haut **8**, fin **16**, bas **8** dp | `Button.kt` L512 (16 dp), L515-521 |
| Taille de l'icône dans un bouton | **18 dp** | `ButtonDefaults.IconSize` (`Button.kt` L537 ; commentaire du code : « TODO update with the correct value in BaselineButtonTokens ») |
| Espace icône-texte | **8 dp** | `ButtonDefaults.IconSpacing = ButtonSmallTokens.IconLabelSpace` (`Button.kt` L543) → `ButtonSmallTokens.kt` L27 |
| Forme | pilule (`CircleShape`, `RoundedCornerShape(50)`) | voir E4 et § 2 |
| Trait de `OutlinedButton` | **1 dp** | `ButtonSmallTokens.OutlinedOutlineWidth` (`ButtonSmallTokens.kt` L30), via `outlinedButtonBorder` (`Button.kt` L898-900) |
| Élévation `Button` | 0 dp (survol 1 dp) | `FilledButtonTokens.kt` L23 et L34 (`ElevationTokens.Level0`, `Level1` : 0 et 1 dp, `ElevationTokens.kt` L24-25) |
| Élévation `OutlinedButton`, `TextButton` | aucune (`elevation = null`) | `Button.kt` L355, L424 |
| Style du texte | `labelLarge` (14 sp / 20 sp / 500 / 0,1 sp) | `Button.kt` L141 |
| `IconButton` : zone tactile | **48 × 48 dp** | `LocalMinimumInteractiveComponentSize` = 48 dp (`InteractiveComponentSize.kt` L196-198), appliquée par `minimumInteractiveComponentSize()` (`IconButton.kt` L178) |
| `IconButton` : conteneur | **40 × 40 dp** (icône 24 dp + 8 dp de chaque côté) | `IconButtonDefaults.smallContainerSize()` (`IconButtonDefaults.kt` L865-883) ← `SmallIconButtonTokens.kt` L24 (hauteur 40), L29 (icône 24), L27 (espace 8) |
| `FloatingActionButton` | **56 × 56 dp**, icône 24 dp, forme `large` 16 dp, élévation 6 dp | `FabBaselineTokens.kt` L24-27 ; `FloatingActionButton.kt` L545 (`FabPrimaryContainerTokens.ContainerElevation` = `ElevationTokens.Level3`, `ElevationTokens.kt` L27 : 6 dp) |
| `SmallFloatingActionButton` | **40 × 40 dp**, icône 24 dp, forme `medium` 12 dp | `FabSmallTokens.kt` L24-27 ; `FloatingActionButton.kt` L202-216 (`sizeIn`) |
| `AssistChip`, `FilterChip` | hauteur **32 dp**, icône 18 dp, forme `small` 8 dp | `AssistChipTokens.kt` L24-25, L56 ; `FilterChipTokens.kt` L24-25, L66 |
| `Card` / `ElevatedCard` | forme `medium` 12 dp ; élévation 0 / 1 dp | `FilledCardTokens.kt` L25-26, `ElevatedCardTokens.kt` L25-26 |

**Décompte des dimensions de boutons relevées :** 19 lignes ci-dessus (hauteur, largeur, 4 jeux
de marges de `Button`/`TextButton` avec et sans icône, icône, espace, forme, trait, 2 élévations,
style de texte, `IconButton` ×2, FAB, petit FAB, chips, cartes). Note : `ButtonSmallTokens.IconSize`
vaut 20 dp (`ButtonSmallTokens.kt` L28) mais `ButtonDefaults.IconSize` impose 18 dp (`Button.kt` L537) ;
le relevé donne celle de `ButtonDefaults`, constante publique que le code appelant utilise pour
dimensionner l'icône ; le bouton lui-même ne la force pas (`Button.kt` L141-153 : aucun `size`). Les boutons que Marculus n'emploie pas
(`FilledTonalButton`, `ElevatedButton` : mêmes dimensions que `Button`, mêmes lignes
`Button.kt` L208-212, L281-285 ; boutons « expressifs » de tailles XS à XL : `ButtonXSmallTokens` …
`ButtonXLargeTokens`, hauteurs non relevées) sont hors relevé.

## 5. Usages dans Marculus v0.65.0 : style commun ou mise en page d'un écran

Recherche sur `app/src/main` au tag : toute occurrence de `Shape`, `RoundedCorner`, `.size(`, `.height(`,
`heightIn`, `ButtonDefaults`, `contentPadding`, `fontSize`, `FontWeight`, `lineHeight`, `letterSpacing`.
« Commun » : un style qu'une autre application peut recopier tel quel (bouton, carte, pilule). « Écran » :
mise en page propre à un écran de Marculus, **non** recopiée dans `terrain-style`. Le classement est
celui du relevé, d'après le code ; **Pascal tranche**.

| Usage dans Marculus | Valeur | Où | Classement |
|---|---|---|---|
| Boutons `Button`, `OutlinedButton`, `TextButton`, `IconButton`, FAB, cartes, puces, boîtes de dialogue sans surcharge | défauts M3 (§ 2, 4) | partout (voir § 4) | **commun** : c'est le défaut M3 |
| Pastille GNSS | `RoundedCornerShape(50)`, marges 10 dp horizontal et 4 dp vertical, icône 14 dp, texte `12.sp` `FontWeight.Medium` blanc, espacement 4 dp | `BadgeFix.kt` L104, L107, L109, L112, L117-118 | **commun pour la forme** (pilule, seule forme explicite) ; **écran pour les marges et le texte** (fonction privée `Pastille`, 5 appels dans `BadgeFix.kt`, couleurs d'état codées en dur `0xFFB71C1C`, `0xFF616161`) |
| Bandeau compact | `Surface(primary / onPrimary)`, hauteur 48 dp, marge 4 dp, texte `titleMedium` + `FontWeight.Medium` | `BandeauCompact.kt` L35-55 | **écran / composant propre à Marculus** (7 écrans l'appellent) ; ni un bouton ni un style M3 ; c'est lui qui montre le vert de `primary` |
| Bouton « + » de la cellule de martelage | `Button`, `fillMaxWidth().height(40.dp)`, `contentPadding = PaddingValues(0.dp)`, texte `titleLarge` | `FeuilleMartelageScreen.kt` L451-457 | **écran** (cellule de 140 × 144 dp, `L87-88`) |
| `IconButton` H, Q et ⋮ de la cellule | `Modifier.size(32.dp)` et `size(28.dp)` | `FeuilleMartelageScreen.kt` L402, L417, L433 | **écran** (réduit en dessous des 40 dp / 48 dp par défaut) |
| Cellule de martelage | `Card` 140 × 144 dp, marge 2 dp, `containerColor = fond` | `FeuilleMartelageScreen.kt` L87-88, L395-397 | **écran** |
| Bouton micro | `FloatingActionButton` imposé à **112 dp** (4 × la surface du FAB de 56 dp, « pour une cible gantée »), icône 48 dp ; version compacte = `SmallFloatingActionButton` par défaut | `DicteeVocale.kt` L366-390 | **écran** (dictée vocale ; dimension de gants, non commune) |
| Menu déroulant du tarif | `OutlinedButton`, `contentPadding = PaddingValues(start = 12.dp, end = 4.dp)` | `CreationContexteScreen.kt` L772-774 | **écran** (un menu avec flèche) |
| Échantillons de couleur | `Box` 36 dp, trait 1 dp | `CreationContexteScreen.kt` L819, L830 | **écran** |
| Ligne de paramètre cliquable, éléments de menu | `heightIn(min = 48.dp)` | `ParametresScreen.kt` L455, L465 | **écran** (reprend la zone tactile de 48 dp) |
| Libellé de cellule | `labelMedium` ou `titleLarge` + `Bold` ; taille automatique 12-24 sp | `FeuilleMartelageScreen.kt` L408-414 | **écran** |
| Graisses `Bold` et `Medium` ajoutées au style | 29 occurrences de `FontWeight.` dans `app/src/main` | `StatutHistoriqueScreen.kt`, `ListeContextesScreen.kt`, `CarteScreen.kt`, `DialogueEtatGnss*.kt` | **écran** (choix de mise en page) |
| Texte de numéro de satellite | `fontSize = 7.sp` sur `labelSmall` | `Skyplot.kt` L105 | **écran** |
| Cartes de la carte | `containerColor = surface.copy(alpha = 0.9f)` ou `0.95f` | `CarteScreen.kt` L709-710, L780-781, L846 | **écran** |

## 6. Écart de teinte HCT entre `reussi.couleur` et `primary`

**Question :** de combien de degrés de teinte HCT `reussi.couleur` s'écarte-t-il du `primary` de
Marculus (`#3E7A40`) ?

**Méthode :** teinte HCT (CAM16 en conditions de visionnage par défaut) calculée par
**`@material/material-color-utilities` 0.4.0** (Google, Apache-2.0 ; paquet npm), classe `Hct`,
`Hct.fromInt(argbFromHex(hex))`, puis écart = plus court arc du cercle des teintes
`min(|a − b|, 360 − |a − b|)`. Script (hors dépôt) : `Hct.fromInt(argbFromHex(hex))`, lecture de `.hue`, `.chroma`, `.tone` ;
exécuté avec Node 22.23.2, avec un crochet de résolution de modules (le paquet 0.4.0 publie des
imports sans extension). Valeurs de `reussi.couleur` lues dans
`terrain-style/src/main/kotlin/fr/nemeton/terrain/style/StyleTerrain.kt` à `origin/main`
(`7c9a5fd`) : L72 (clair) `Semantique(0xFF00341F, …)`, L126 (sombre) `Semantique(0xFFBBFFD7, …)`.
**Un seul outil tiers**, pas deux ; la parité avec l'implémentation Kotlin du dépôt
(`terrain-style/generation/mcu/hct/Hct.kt`) n'a pas été rejouée.

| Couleur | Teinte HCT | Chroma | Ton |
|---|---|---|---|
| `primary` `#3E7A40` (clair et sombre) | 146,83° | 42,91 | 46,06 |
| `reussi.couleur` clair `#00341F` | 162,19° | 28,05 | 18,17 |
| `reussi.couleur` sombre `#BBFFD7` | 162,78° | 34,81 | 94,73 |
| graine de `reussi` `#0F8558` (pour information) | 162,52° | 47,07 | 48,98 |

| Thème | Écart de teinte `reussi.couleur` − `primary` |
|---|---|
| Clair | **15,36°** (`reussi` plus bleu que `primary`) |
| Sombre | **15,95°** (idem) |
| Graine `#0F8558` (pour information) | 15,69° |

À lire avec prudence : `#00341F` est très sombre (ton 18) et `#BBFFD7` très clair (ton 95) ; à ces
tons la teinte perçue est moins nette que pour `#3E7A40` (ton 46). L'écart entre `primary` et la
graine, à ton comparable (49 contre 46), est de 15,69° : c'est lui qui dit la distance « de
couleur » ; les deux autres disent la distance des variantes sombre et claire. Le commentaire de
`StyleTerrain.kt` L9 (« clair 17.7°, sombre 16.1° ») porte sur l'ancien `primary` généré
(`#202F2B` / `#E2F4ED`) et n'est plus la mesure à jour. Aucun seuil n'est posé ici : dire si 15°
est « assez proche » ou « assez distinct » est une décision de Pascal.

## 7. Ce qui n'a pas pu être relevé

- La **version résolue** de `material3` par une compilation de Marculus (voir « Sources »).
- Les **valeurs au pixel** : aucune capture, aucun rendu ; les tailles sont celles des jetons.
- Les **boutons de Material 3 « expressifs »** (tailles XS à XL, groupes, boutons scindés) :
  Marculus n'en emploie aucun ; non relevés.
- Les **couleurs d'état de Marculus codées en dur** (`0xFFB71C1C`, `0xFF616161`, `couleurQualite`,
  couleurs de la fiche, de la carte, du statut) : hors des 48 rôles, non relevées.
- La **parité Kotlin** de l'écart HCT (un outil tiers seulement).
- L'**avis du `relecteur`** : non demandé à ce stade.

## 8. Changer ce relevé

On change d'abord Marculus, puis le tag de référence ici et dans `terrain-style`, par une note
datée de l'ADR 0006. Jamais ici seul.
