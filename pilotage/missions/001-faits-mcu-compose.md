# Mission 001 — faits-mcu-compose

> Fiche de mission (fiches n° 9 et 15 de la méthode). Une mission = une phrase = une
> branche = une PR. Tirée de `docs/spec.md` (validée le 2026-10-23), issue #1.

## Mission

**En une phrase (quoi, et pourquoi) :** Établir, sources primaires à l'appui, les faits de Material Color Utilities et de Compose Material 3 dont dépendent les ADR 0001 et 0002, pour que Pascal les tranche et que le `dev` sache quoi compiler.

**Redite par l'agent :** établir, sources primaires à l'appui, les faits MCU et Compose Material 3
dont dépendent les ADR 0001 et 0002, afin que Pascal tranche et que le `dev` sache quoi compiler
(recopiée par l'orchestrateur depuis `docs/referentiels/mcu-compose.md`, où l'acquéreur l'a écrite).

## Les trois questions (règle de staffing)

| Question | Réponse | Conséquence |
|---|---|---|
| Peut-il redire la mission en une phrase, avec son but ? | oui | — |
| Sait-il comment faire ? (sources, conventions nommées ci-dessous) | oui : besoins 1 et 2 de la spec, objections du `contradicteur` au journal | — |
| A-t-il les moyens ? (outils, données d'essai, tests) | oui : WebFetch sur les dépôts publics | — |

**Temps de la main :** appuyer — la mission éclaire une décision d'architecture, l'agent rend ses constats sans conclure.

## Ma réponse d'abord

_Rempli par Pascal seul, avant de lire le travail de l'agent : ce qu'il attend de la
mission, et le résultat qu'il prévoit. Une ligne suffit. L'orchestrateur ne le remplit
pas._

## L'Homme

- **Agent :** `acquereur`
- **Modèle :** opus (justesse des données qui fondent l'architecture)
- **Ce qui ne lui est pas demandé :** choisir entre les options des ADR ; écrire du code ; mesurer la parité Java/Kotlin par exécution (mission 006).

## Harnais

- **Peut :** écrire `docs/referentiels/mcu-compose.md`, ouvrir une PR.
- **Ne doit pas :** toucher à `main`, sortir du périmètre, inventer une valeur ou une source, trancher une question de valeur (la poser à Pascal)
- **Sources autorisées :** `docs/spec.md` (besoins 1 et 2), `docs/adr/0001-*.md`, `docs/adr/0002-*.md`, dépôt material-foundation/material-color-utilities, référence Compose `ColorScheme`, licence Apache-2.0.

## Tests

- **Comment saura-t-on que c'est juste :** chaque fait cité avec URL, fichier, ligne et commit : commit MCU proposé à épingler ; imports et version de Kotlin des sources `kotlin/` ; visibilité du constructeur de `DynamicScheme` qui prend `errorPalette`, et usage de `errorPalette` par `error()` en SPEC_2021 ; comportement des `*Dim` dans un schéma 2021 ; liste des propriétés de `ColorScheme` à une version datée ; obligations Apache-2.0 §4 ; deux sources comparées quand c'est possible.

## Terrain

- **Échantillon que Pascal vérifiera :** la liste des rôles de `ColorScheme` et le commit MCU proposé.

## Qui veille, qui tranche

- **Relecteur :** `relecteur`, puis Pascal.
- **Critère d'arrêt de la relecture :** 3 allers-retours ; au-delà, la décision remonte à
  Pascal.
- **Point de suivi :** à la PR, puis avant que Pascal tranche les ADR 0001 et 0002.
- **Qui tranche :** Pascal.

## Comment saura-t-on que ça marche ?

Pascal peut remplir les colonnes « Prêts à payer ? » des ADR 0001 et 0002 sans question en suspens.

## Remarques du relecteur — aller-retour 1/3 (2026-10-23)

À reprendre dans `docs/referentiels/mcu-compose.md`, sans rien trancher :

1. **E3 et §1.3 sont faux** : huit fichiers Java importent `androidx.annotation` ou
   `errorprone` : `ColorSpec.java`, `ColorSpec2021.java` (dont `errorprone.annotations.Var`),
   `ColorSpec2025.java`, `ColorSpec2026.java`, `DynamicColor.java`,
   `MaterialDynamicColors.java`, `ToneDeltaPair.java`, `scheme/Scheme.java`.
2. **Dater `ColorScheme`** : ajouter comme source datée le jar de sources de Compose
   Material 3 **1.4.0** publié (`https://dl.google.com/android/maven2/androidx/compose/material3/material3-android/1.4.0/material3-android-1.4.0-sources.jar`),
   qui donne les mêmes 48 rôles, dans le même ordre.
3. **Numéros de ligne** à corriger (au commit `bd811e4`) : `DEFAULT_SPEC_VERSION` /
   `DEFAULT_PLATFORM` 290-291 (pas 335-336) ; `maybeFallbackSpecVersion` Kotlin 399-417
   (pas 394-416) ; `init` 86 (pas 88) ; `class ColorSpec2025` 29 (pas 30) ; `errorDim` de
   `ColorSpec2025` 1258 (pas 1253) ; `error()` Java 659 (pas 660). Revérifier toutes les
   autres lignes citées.
4. **Compléter** : les imports des 43 `.kt` sont tous internes ou `java.text.*`,
   `java.util.*`, `kotlin.math.*` (la réserve « 26 fichiers non lus » tombe, seule la
   compilation Kotlin 2.2 reste) ; `ContrastCurve.get` renvoie `high` dès
   `contrastLevel ≥ 1,0` ; E1 : `CMF` garde toujours la version demandée (« toute autre
   variante » est inexact).
5. Remplir « Redite par l'agent » en tête de fiche.

Copie locale de MCU au commit `bd811e4`, lisible avec Read/Grep :
`/tmp/claude-1000/-home-pascal-dev-terrain/d8ba4326-e834-46a8-bb1a-c68e4800fb57/scratchpad/mcu`
(vérifier le commit dans `.git` avant de s'y fier).

## Remarques du relecteur — aller-retour 2/3 (2026-10-23)

Fond tenu ; trois points de forme, dans `docs/referentiels/mcu-compose.md` seulement :

1. **Commit `bd811e4` retenu par Pascal** (ADR 0001, commit `a9da41d`) : le dire partout où
   il est encore « proposé » ou « à trancher » (L44, L52-54, point au sol 2 L184 — qui
   tombe et se remplace —, journal proposé L193).
2. **Jar Compose 1.4.0 vérifié** deux fois (orchestrateur et relecteur, sha256
   `3e3209422a382566a780e2c1410edd866e4474498098fdbcf942c690ade1ddaa`) : lever la réserve
   (L18, §2.1 L152, L157-159). **Point au sol 1 faux** : en 1.4.0, `class ColorScheme(`
   (L148) déclare 48 `val …: Color` sans `public` (`grep -c 'public val'` = 0) ; écrire
   « compter 48 `val … : Color` » et noter la différence avec la tête `androidx-main`.
3. La redite est recopiée dans la fiche par l'orchestrateur : rien à faire.
