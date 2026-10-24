# Mission 025 — contrôle des recopies (plugin et terrain)

> Fiche de mission. Tirée du bilan n° 3 du 2026-10-24 (cause G du Pareto, décision de Pascal).

## Mission

**En une phrase (quoi, et pourquoi) :** Ajouter à la liste de contrôle du plugin un point « citations, noms et versions comparés mécaniquement à leur source », et à terrain un test qui compare les coordonnées de version du README au dernier tag, parce que 5 des 28 incidents du bilan n° 3 (cause G) sont des recopies inexactes.

**Redite par l'agent :** _à compléter._

**Temps de la main :** appuyer.

## L'Homme

- **Agent :** `archiviste` pour le plugin, `testeur` pour le test de terrain (deux PR).

## Harnais

- **Peut :**
  - plugin : `skills/methode/liste-de-controle.md` et ses tests ; version 0.7.x ;
  - terrain : un test dans un module existant, ou une étape de CI, qui lit `README.md` et `git describe --tags`.
- **Ne doit pas :** sortir de ces fichiers.

## Dépend de / rouge attendu jusqu'à

Dépend de : 024 pour le plugin (même fichier de version, fusion après 024). CI rouge attendue : aucune.

## Tests

- tests verts ;
- le test de terrain échoue si le README cite une version différente du dernier tag.

## Terrain

- **Échantillon :** aucun directement.

## Ma réponse d'abord

_Rempli par Pascal seul, avant de lire le travail de l'agent._

## Valeur provisoire

Aucune.

## Qui veille, qui tranche

- **Relecteur :** `relecteur`, puis Pascal.
- **Critère d'arrêt de la relecture :** 3 allers-retours.
- **Qui tranche :** Pascal.
