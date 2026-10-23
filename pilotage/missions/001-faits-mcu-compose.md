# Mission 001 — faits-mcu-compose

> Fiche de mission (fiches n° 9 et 15 de la méthode). Une mission = une phrase = une
> branche = une PR. Tirée de `docs/spec.md` (validée le 2026-10-23), issue #1.

## Mission

**En une phrase (quoi, et pourquoi) :** Établir, sources primaires à l'appui, les faits de Material Color Utilities et de Compose Material 3 dont dépendent les ADR 0001 et 0002, pour que Pascal les tranche et que le `dev` sache quoi compiler.

**Redite par l'agent :** _à compléter par l'agent avant d'agir — s'il n'y arrive pas, il s'arrête._

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
