# ADR 0004 — variante `CONTENT` à la place de `FIDELITY`, pour distinguer tertiary d'error

- **Date :** 2026-10-23
- **Statut :** acceptée par Pascal le 2026-10-23 ; amende l'ADR 0001 (réglage « variante »)
- **Proposée par :** `dev` de la mission 007 (mesures), Claude (orchestrateur)
- **Tranchée par :** Pascal

## La décision en une phrase

Les schémas de `terrain-style` sont générés en variante `CONTENT` de Material Color
Utilities au lieu de `FIDELITY`, tous les autres réglages de l'ADR 0001 restant identiques,
pour qu'en thème sombre `tertiary` ne soit plus identique à `error`.

## Contexte

Le nuancier de la mission 006 (FIDELITY, `#374742`, SPEC_2021, contraste 1,0) donne en
thème sombre `tertiary` = `error` = `#FFECE9` (ΔE00 = 0, dans toutes les visions). Cause,
d'après les mesures de la mission 007 :

- la palette tertiaire de FIDELITY vient du complément de la graine
  (`TemperatureCache.complement`, HCT ≈ 26 / 12 / 29), de même teinte que la palette
  d'erreur (25 / 84) — `ColorSpec2021.kt:1160-1163` et `:1244-1252` ;
- `tertiary` et `error` ont la même fonction de ton, le même fond et la même courbe
  `ContrastCurve(3.0, 4.5, 7.0, 7.0)` (`ColorSpec2021.kt:506-531`, `:603-635`) : au
  contraste 1,0 ils montent tous deux au ton 94,8 en sombre ;
- à ce ton et à cette teinte, le gamut sRGB plafonne le chroma à ≈ 6,1
  (`HctSolver.kt:633-643`) : les deux rôles tombent sur la même couleur.

Options mesurées (mission 007, aucune n'ajoute de rôle interdit en texte) :

| Option | Effet | Coût |
|---|---|---|
| A. Documenter tertiary « pas pour un état » | aucun | l'utilisateur lit un accent comme une erreur |
| **B. Variante `CONTENT`** (retenue) | ne change que les rôles `tertiary*` (violet-gris, h ≈ 285 : `#2B2B38` clair, `#F1EEFF` sombre) ; ΔE00 à error 26,2 (clair) et 9,8 (sombre), deutéranope ≥ 11,1 | amende l'ADR 0001, régénère la 006 |
| C. Palette tertiaire TONAL_SPOT passée au constructeur | tertiary bleu ; bien distinct d'error | assemblage de deux variantes non documenté par MCU |
| D. Teinte sourcée (marque) | non mesurée | acquisition d'abord |
| E. Baisser le contraste | — | contraire à la spec (contraste 1,0) |

`CONTENT` garde, comme `FIDELITY`, la graine en `primaryContainer` (`#374742` en clair) :
`primary`, `secondary`, les neutres et `error` sont inchangés. En SPEC_2021 sur `PHONE`,
`maybeFallbackSpecVersion` retombe aussi sur `SPEC_2021` pour `CONTENT` (seul
`CONTENT` sur `WATCH` est conservé) : le script continue de le vérifier.

## Le crible FRANCE

| Lettre | Question | Réponse (une ligne) | Ce que ça coûte | Prêts à payer ? |
|---|---|---|---|---|
| **F**rugale | La plus simple qui tienne ? | un seul réglage MCU, sans palette sur mesure | une régénération | inconnu |
| **R**esponsable | Qui fait quoi ? | `dev` 006 régénère ; le relecteur rejoue ; Pascal regarde le nuancier | — | inconnu |
| **A**nticiper | Et si une autre graine recrée la collision ? | le choix tient pour `#374742` ; une autre graine se remesure | — | inconnu |
| **N**ovateur | Toutes les idées ? | cinq options mesurées ou écartées | — | — |
| **C**oût | Dépense inutile ? | non : la collision se paierait sur le terrain | — | inconnu |
| **É**cologique | Réutilisation ? | variante publiée de MCU | — | inconnu |

## Valeurs sacrifiées

- **La variante choisie au départ** (`FIDELITY`, ADR 0001) : remplacée par sa voisine
  `CONTENT`, qui garde la graine visible.
- **Le complément chaud de la graine** comme tertiaire : remplacé par un analogue froid.

## Sources

- Mesures et code cités : mission 007, rendu du 2026-10-23 (tableaux §2.2 à §2.5).
- Material Color Utilities au commit `bd811e4`, `kotlin/dynamiccolor/ColorSpec2021.kt`,
  `kotlin/hct/HctSolver.kt`, `kotlin/dynamiccolor/DynamicScheme.kt`.
- Nuancier de la mission 006, `terrain-style/generation/nuancier.svg` (777a914).

## Question de contrôle

Cette décision tient-elle nos six valeurs, et que sommes-nous prêts à payer pour cela ?
