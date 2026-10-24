# Mission 021 — tests d'égalité au style de Marculus

> Fiche de mission (fiches n° 9 et 15 de la méthode). Une mission = une phrase = une
> branche = une PR. Tirée de l'ADR 0006 (cap de Pascal du 2026-10-24 : le style de Marculus est la règle).

## Mission

**En une phrase (quoi, et pourquoi) :** Écrire, sans lire l'implémentation, les tests qui exigent que `StyleTerrain.clair` et `StyleTerrain.sombre` (48 rôles), `FormesTerrain`, `TypoTerrain` et `BoutonsTerrain` soient égaux, valeur par valeur, au référentiel `docs/referentiels/style-marculus.md` et à son CSV, et remplacer les tests de 7:1 par un test informatif, pour que le style de Marculus soit garanti par des tests qu'il n'a pas écrits (ADR 0006).

**Redite par l'agent :** _à compléter par l'agent avant d'agir._

**Temps de la main :** appuyer.

## L'Homme

- **Agent :** `testeur`, sonnet
- **Ce qui ne lui est pas demandé :** écrire le code ; lire `terrain-style/src/main` au-delà des signatures publiques.

## Harnais

- **Peut :** réécrire `terrain-style/src/test/kotlin/**/JetonsTest.kt` et les tests qui imposent 7:1 ; ajouter des tests pour les formes, la typographie et les boutons ; garder les tests de `Contraste` (table de deux outils tiers) ; pousser sur la branche de 020 ; PR en brouillon.
- **Ne doit pas :** calculer une valeur attendue avec le code testé ; modifier le référentiel.
- **Sources :** spec (partie `terrain-style`, révisée le 2026-10-24), ADR 0006, référentiel et CSV de 020.

## Valeur provisoire

Aucune.

## Dépend de / rouge attendu jusqu'à

- **Dépend de :** 020 ; part de sa branche.
- **Ordre de fusion :** groupée 020 → 021 → 022.
- **CI rouge attendue :** les tests d'égalité et les nouveaux objets (`FormesTerrain`, `TypoTerrain`, `BoutonsTerrain`) échouent ou ne compilent pas jusqu'à 022.

## Tests

- **Comment saura-t-on que c'est juste :** chaque valeur du référentiel a une assertion d'égalité ; le test de contraste affiche les rapports sans échouer ; `interditsEnTexte` vaut `{alerte.couleur}`.

## Terrain

- **Échantillon que Pascal vérifiera :** aucun directement.

## Ma réponse d'abord

_Rempli par Pascal seul, avant de lire le travail de l'agent. L'orchestrateur ne le remplit pas._

## Qui veille, qui tranche

- **Relecteur :** `relecteur`, puis Pascal.
- **Critère d'arrêt de la relecture :** 3 allers-retours ; au-delà, la décision remonte à Pascal.
- **Point de suivi :** à la PR.
- **Qui tranche :** Pascal.
