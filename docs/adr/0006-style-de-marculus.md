# ADR 0006 — `terrain-style` reprend le style de Marculus

- **Date :** 2026-10-24
- **Statut :** acceptée par Pascal le 2026-10-24 (« c'est la règle et la loi et ma décision ») ;
  « Prêts à payer ? » : inconnu
- **Proposée par :** Pascal (cap), rédigée par Claude (orchestrateur)
- **Tranchée par :** Pascal
- **Remplace :** ADR 0001 (jetons générés par Material Color Utilities depuis `#374742`), ADR 0004
  (variante `CONTENT`) et la règle « 7:1 pour tous les couples » de la spec et du brief

## La décision en une phrase

Le style de `terrain-style` est celui de Marculus, et il s'impose à Groma et à Surculus : couleurs,
formes, typographie et dimensions des boutons. Tout ce que Marculus ne fixe pas vaut la valeur par
défaut de Material 3, à la version de Compose qu'utilise Marculus.

## Contexte

- `terrain-style` v0.6.0 générait ses 48 rôles par Material Color Utilities depuis `VertMarculus
  #374742`, au contraste 1,0. Le `primary` obtenu était `#202F2B` en clair et `#E2F4ED` en sombre.
- Marculus a adopté ces jetons en v0.64.0, puis les a retirés en v0.64.2. Sur le téléphone de
  Pascal, « le bandeau principal n'est plus vert » (brief
  `~/dev/briefs/vers-terrain/2026-10-24-marculus-palette-reste-verte.md`).
- Le même jour, Pascal a jugé au soleil les thèmes actuels de Marculus (v0.65.0) « parfaits », en
  clair et en sombre, alors que le blanc sur `VertVif #3E7A40` ne fait que 5,17:1.
- Le même jour, Pascal a changé de cap : « je veux que Marculus, groma et surculus utilise
  terrain-style mais avec un terrain-style qui conserve les couleurs actuelles de vert de marculus
  et qui vont donc s'imposer à groma et surculus » (journal de Marculus, ligne `cap` du 2026-10-24 ;
  ADR 0005 de Marculus, révision du 2026-10-24).
- Dans terrain, Pascal a étendu ce cap à tout le style : « les couleurs, forme, bouton, etc. de
  terrain-style doivent reprendre le style de marculus » (journal de terrain, 2026-10-24).

Options envisagées : garder la génération MCU en changeant de graine (brief de Marculus, § 2,
remplacé par sa révision) ; reprendre Marculus. Pascal a tranché pour reprendre Marculus.

## Ce qui est décidé

- **Source unique** : le thème de Marculus, à un tag nommé. Aujourd'hui c'est v0.65.0,
  `app/src/main/kotlin/io/github/pobsteta/marculus/ui/theme/Theme.kt`, l. 10-28.
- **Couleurs, en clair comme en sombre** :

  | Rôle | Clair | Sombre |
  |---|---|---|
  | `primary` | `VertVif #3E7A40` | `VertVif #3E7A40` |
  | `onPrimary` | blanc `#FFFFFF` | blanc `#FFFFFF` |
  | `primaryContainer` | `VertMarculus #374742` | `VertMarculus #374742` |
  | `onPrimaryContainer` | `Creme #F5EBD8` | `Creme #F5EBD8` |
  | `secondary` | `VertMarculus #374742` | `Creme #F5EBD8` |
  | `surfaceTint` (non fixé, suit `primary` dans Material 3) | `#3E7A40` | `#3E7A40` |
  | les 42 autres rôles | défaut de `lightColorScheme()` | défaut de `darkColorScheme()` |

  Les valeurs par défaut sont celles de Compose Material 3 au BOM de Marculus (`2026.02.01`,
  `gradle/libs.versions.toml` l. 5). Elles sont relevées dans
  `docs/referentiels/style-marculus.md` (mission 020).
- **Formes, typographie, boutons** : Marculus ne fixe dans son thème que les couleurs ; ses
  formes, sa typographie et ses boutons sont donc les valeurs par défaut de Material 3 1.4.0
  (BOM `2026.02.01`) : `Shapes` (4, 8, 12, 16, 28 dp), les 15 styles de `Typography`, et des
  boutons en pilule (`RoundedCornerShape(50)`), hauteur 40 dp. Ces valeurs sont relevées dans le même référentiel.
- **Les 42 rôles par défaut** sont la palette de base de Material 3, violette et rose (par
  exemple `tertiary` `#7D5260`, `background` `#FEF7FF`). C'est ce que Marculus affiche
  aujourd'hui, et donc la règle.
- **Kotlin pur, JVM 17, sans Android ni Compose** (décision du 2026-10-17) : les formes et la
  typographie sont exposées en nombres (dp, sp, graisse) et en couleurs ARGB. Chaque application
  les recopie dans son `MaterialTheme`.
- **La règle 7:1 tombe comme règle commune.** `Contraste` reste publique pour que chaque
  application mesure ce qu'elle veut. Un rapport sous 7:1 n'empêche plus la publication.
- **`reussi` et `alerte` restent** (ADR 0002) : `reussi` de graine `#0F8558`, gelé le
  2026-10-24 (`reussi.couleur` vaut `#00341F` en clair et `#BBFFD7` en sombre), et
  `alerte` `#E2AA00`. Comme le demande Marculus, l'écart de teinte entre `reussi` et le nouveau
  `primary` (`VertVif`) est mesuré : 15,4° en clair, 16,0° en sombre (mission 020).
- **Une palette, pas une par application** : aucun écart propre à Groma ou à Surculus.
- **Version : v0.7.1.** La v0.6.1, publiée le 2026-10-24, porte encore la palette générée.
- **Avant publication, le nuancier est montré à Pascal** (brief de Marculus, § 2.3). Les tests
  vérifient l'égalité au relevé, rôle par rôle. Marculus montre ensuite une capture avant/après
  identique (sa mission 006).
- **Changer le style** : on change d'abord Marculus, puis le tag de référence dans le
  référentiel et ici, par une note datée. Jamais dans terrain seul.

## Le crible FRANCE

| Lettre | Question | Réponse (une ligne) | Ce que ça coûte | Prêts à payer ? |
|---|---|---|---|---|
| **F**rugale | La plus simple qui tienne ? | recopier des valeurs relevées, sans génération | — | inconnu |
| **R**esponsable | Qui fait quoi ? | Marculus porte le style ; terrain le publie ; Groma et Surculus le prennent | Groma et Surculus changent d'aspect | inconnu |
| **A**nticiper | Et si l'imprévu arrive ? | un défaut de lisibilité se corrige dans Marculus, puis remonte ici | plus de garantie 7:1 par construction | inconnu |
| **N**ovateur | Toutes les idées ? | génération MCU avec une autre graine, écartée par Pascal | — | — |
| **C**oût | Dépense inutile ? | le script de génération MCU n'est plus la source des 48 rôles | travail de la v0.6.0 en partie perdu | inconnu |
| **É**cologique | Réutilisation ? | un seul style pour trois applications | — | inconnu |

## Valeurs sacrifiées

- **7:1 garanti par un test.** Remplacé par le jugement de Pascal au soleil (« parfait », 2026-10-24).
- **Indépendance du style vis-à-vis d'une application.** Marculus devient la référence.

## Sources

- Marculus : `Theme.kt` au tag v0.65.0, l. 10-28 ; ADR 0005, révision du 2026-10-24 ; journal,
  ligne `cap` du 2026-10-24 ; mission 006.
- Brief `~/dev/briefs/vers-terrain/2026-10-24-marculus-palette-reste-verte.md` (révision du 2026-10-24).
- Journal de terrain, ligne `cap` du 2026-10-24.

## Question de contrôle

Cette décision tient-elle nos six valeurs, et que sommes-nous prêts à payer pour cela ?

## Note du 2026-10-24 : version v0.7.1

La v0.7.0 (`terrain-voix`) a été publiée avant le style de Marculus. Le style sort donc en **v0.7.1**, et non en v0.6.2, qui se trierait avant v0.7.0. Les mentions « v0.6.2 » de ce dépôt sont remplacées par v0.7.1.
