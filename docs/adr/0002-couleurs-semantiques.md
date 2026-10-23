# ADR 0002 — couleurs sémantiques communes (réussi, alerte, erreur)

- **Date :** 2026-10-23
- **Statut :** proposée ; erreur, alerte (dont sa valeur) et repli arbitrés par Pascal le 2026-10-23
- **Proposée par :** Claude (orchestrateur), sur une proposition du `contradicteur`
- **Tranchée par :** Pascal

## La décision en une phrase

**Erreur** est le rôle `error` de Material (palette par défaut de MCU, non harmonisée,
exposé une seule fois) ; **réussi** est généré par MCU avec les mêmes courbes de contraste
qu'`error` ; **alerte** suit la convention de sécurité, jaune clair en fond et noir dessus,
figé hors de MCU, son jaune étant interdit en texte.

## Contexte

Pascal a retenu des couleurs sémantiques communes : ce sont elles que le forestier lit en
passant d'une application à l'autre. Constats sourcés :

- Material 3 ne fournit qu'`error`. Les « custom colors » de MCU (`customColor`, en
  TypeScript seulement) ont des **tons fixes** (40/100/90/10 en clair, 80/20/30/90 en
  sombre), sans niveau de contraste ; MDC-Android demande de les ajouter à la main aux
  surcouches de contraste moyen et élevé.
- Le constructeur de `DynamicScheme` accepte une `errorPalette` (par défaut teinte 25,
  chroma 84). Un schéma construit avec la palette de « réussi » donne des rôles `error*`
  aux courbes de contraste d'`error` : c'est le mécanisme retenu pour réussi, **à vérifier
  en premier** (visibilité publique du constructeur).
- En `SPEC_2021` au niveau 1,0, les conteneurs ont la courbe `(1.0, 1.0, 3.0, 4.5)` contre
  `surfaceDim` : ils sont poussés vers le sombre. Un jaune devient olive ou brun, et
  `DislikeAnalyzer` classe « déplaisantes » les teintes 90°–111° sombres et saturées. Le
  jaune d'alerte ne serait plus reconnaissable : d'où la convention de sécurité.
- `Blend.harmonize` tourne la teinte de la moitié de l'écart, au plus 15° : vers une graine
  verte, « réussi » se rapproche de `primary`.

Options envisagées : (1) erreur = `error`, réussi par `errorPalette`, alerte selon la
convention de sécurité — **retenue** ; (2) les trois harmonisées et générées ; (3) trois
couleurs fixes à la main ; (4) rien dans le module.

**Valeurs choisies par Pascal le 2026-10-23** (sur `docs/referentiels/couleurs-etat.md`,
mission 002) : alerte = **`#E2AA00`**, jaune de sécurité d'ISO 3864-4 (Table 1, β 0,45, D65,
converti en sRGB ; un ambre plutôt qu'un « jaune clair »), texte noir `#000000` (9,98:1) ;
le vert de départ de « réussi » attend la mesure HCT de la mission 007 ; ISO 3864-1 et
l'annexe E d'ISO 3864-4 ne sont pas achetées pour l'instant.

Restent à acquérir (`acquereur`) : la teinte de référence de « réussi » et la décision
d'harmoniser ou non (mesure de l'écart HCT à `primary`) ; la valeur sRGB sourcée du jaune
de sécurité et de sa couleur de contraste (ISO 3864-1 ; ISO 3864-4 pour la colorimétrie ;
ISO 22324 et IEC 60073 à comparer).

## Le crible FRANCE

| Lettre | Question | Réponse (une ligne) | Ce que ça coûte | Prêts à payer ? |
|---|---|---|---|---|
| **F**rugale | La plus simple qui tienne ? | erreur ne coûte rien ; réussi réutilise la chaîne MCU ; alerte = deux valeurs figées | un schéma MCU de plus ; un mécanisme `errorPalette` à vérifier | _à trancher_ |
| **R**esponsable | Qui fait quoi ? | `acquereur` source les teintes ; Pascal les choisit ; `dev` génère ; le test juge | — | _à trancher_ |
| **A**nticiper | Et si un rôle ne tient pas 7:1 ? | il est listé et documenté « interdit en texte » (arbitrage de Pascal) ; si `errorPalette` n'est pas accessible, retour à Pascal | — | oui (Pascal, 2026-10-23) |
| **N**ovateur | Toutes les idées ? | quatre options, dont le statu quo et la convention de sécurité | — | — |
| **C**oût | Dépense inutile ? | évite trois palettes d'état divergentes | Surculus reprend ses trois états communs | _à trancher_ |
| **É**cologique | Réutilisation ? | `error` de Material, convention de sécurité publiée | — | _à trancher_ |

## Valeurs sacrifiées

- **Une seule chaîne de génération** : le jaune d'alerte sort de MCU, pour rester
  reconnaissable (arbitrage de Pascal).
- **Le jaune en texte** : `alerte.couleur` est interdit en texte.
- **Autonomie de chaque application** sur ses états communs.
- **Distinction réussi / `primary`**, si l'on harmonise « réussi » : à mesurer avant de
  trancher.

## Sources

- MCU : `typescript/utils/theme_utils.ts` (`customColor`), `java/dynamiccolor/DynamicScheme.java`
  (`errorPalette`), `ColorSpec2021` (courbes des conteneurs), `java/dislike/DislikeAnalyzer.java`,
  `java/blend/Blend.java` (`harmonize`) — https://github.com/material-foundation/material-color-utilities
- MDC-Android, `docs/theming/Color.md` (couleurs personnalisées et contraste) —
  https://github.com/material-components/material-components-android/blob/master/docs/theming/Color.md
- ISO 3864-1 (couleurs de sécurité et de contraste) et ISO 3864-4 (colorimétrie), textes
  payants, à acquérir (références exactes à fournir par l'`acquereur`)
- ISO 22324:2022 (alertes codées par couleur), aperçu —
  https://webstore.ansi.org/preview-pages/ISO/preview_ISO+22324-2022.pdf
- WCAG 2.2, 1.4.1 (ne pas coder par la seule couleur) — https://www.w3.org/TR/WCAG22/#use-of-color

## Question de contrôle

Cette décision tient-elle nos six valeurs, et que sommes-nous prêts à payer pour cela ?
