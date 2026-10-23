# ADR 0002 — couleurs sémantiques communes (réussi, alerte, erreur)

- **Date :** 2026-10-23
- **Statut :** proposée
- **Proposée par :** Claude (orchestrateur), sur une proposition du `contradicteur`
- **Tranchée par :** Pascal

## La décision en une phrase

`terrain-style` fournit trois couleurs sémantiques, réussi, alerte et erreur, chacune en
quatre rôles (couleur, texte dessus, conteneur, texte sur conteneur), générées par MCU
depuis une teinte de référence publiée, harmonisée vers VertMarculus, au même contraste
1,0 et soumises au même test de 7:1.

## Contexte

Pascal a retenu le 2026-10-23 des couleurs sémantiques communes : ce sont elles que le
forestier lit en passant d'une application à l'autre. Material 3 ne fournit qu'`error` ;
MDC-Android appelle « custom colors » les autres couleurs, et recommande de les
**harmoniser** vers la couleur de départ (`Blend.harmonize` de MCU).

Options envisagées :

1. `erreur` = rôles `error` du schéma ; `reussi` et `alerte` = couleurs personnalisées
   harmonisées, déclinées en tons comme `error`.
2. Trois couleurs fixes choisies à la main, vérifiées par le test seulement.
3. Teintes des couleurs de sécurité ISO 3864-4 (vert, jaune, rouge) sans harmonisation.
4. Rien dans le module (statu quo de l'issue #1) : chaque application garde les siennes.

La teinte de départ de `reussi` et `alerte` reste à acquérir (`acquereur`, spec, besoin 3).

## Le crible FRANCE

| Lettre | Question | Réponse (une ligne) | Ce que ça coûte | Prêts à payer ? |
|---|---|---|---|---|
| **F**rugale | La plus simple qui tienne ? | Option 1 : même chaîne que les autres rôles, même test | un réglage de plus par couleur | _à trancher_ |
| **R**esponsable | Qui fait quoi ? | `acquereur` propose les teintes ; Pascal les choisit ; `dev` les génère | — | _à trancher_ |
| **A**nticiper | Et si une couleur ne tient pas 7:1 ? | même règle : pas de retouche, Pascal choisit | « alerte » (jaune) tient mal en conteneur clair | _à trancher_ |
| **N**ovateur | Toutes les idées ? | quatre options, dont le statu quo | — | — |
| **C**oût | Dépense inutile ? | évite trois palettes d'état divergentes | Surculus doit reprendre ses trois états communs | _à trancher_ |
| **É**cologique | Réutilisation ? | convention MDC « custom colors », teintes de référence publiées | — | _à trancher_ |

## Valeurs sacrifiées

- **Autonomie de chaque application** sur ses états communs : Surculus, Marculus et Groma
  prennent les trois couleurs du module.
- **Fidélité stricte aux couleurs de sécurité** (si option 1) : l'harmonisation décale un
  peu la teinte vers VertMarculus.

## Sources

- MDC-Android, `docs/theming/Color.md` (couleurs personnalisées, harmonisation) —
  https://github.com/material-components/material-components-android/blob/master/docs/theming/Color.md
- MCU, `java/blend/Blend.java` (`harmonize`) —
  https://github.com/material-foundation/material-color-utilities
- ISO 3864-4:2011, couleurs de sécurité (texte payant, à acquérir) — https://www.iso.org/standard/54434.html
- WCAG 2.2, 1.4.1 (ne pas coder par la seule couleur) — https://www.w3.org/TR/WCAG22/#use-of-color

## Question de contrôle

Cette décision tient-elle nos six valeurs, et que sommes-nous prêts à payer pour cela ?
