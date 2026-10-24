# Mission 023 — corrections du bilan n° 3

> Fiche de mission. Tirée du bilan n° 3 du 2026-10-24 (rapport de l'archiviste, décision de Pascal).

## Mission

**En une phrase (quoi, et pourquoi) :** Appliquer à terrain les corrections P1 et P2 de l'archiviste du bilan n° 3 (bandeaux datés dans le brief et la spec, `jetons.csv` marqué ou réduit à `reussi` et `alerte`, `equipe.md` en v0.7.1, statut de l'ADR 0002, tableau « Versions » et note « Migrer de v0.6.x vers v0.7.0 » dans le README, « sur ordre de Pascal » pour le tag), pour qu'un successeur ne lise plus jamais l'ancienne règle comme si elle était en vigueur.

**Redite par l'agent :** _à compléter._

**Temps de la main :** appuyer.

## L'Homme

- **Agent :** `archiviste`, sonnet.

## Harnais

- **Peut :**
  - modifier `docs/brief.md`, `docs/spec.md` et `docs/adr/0002-couleurs-semantiques.md`, par des lignes ou bandeaux datés, en ajout seul pour les arbitrages et les ADR ;
  - modifier `README.md`, `pilotage/equipe.md` (ligne 022 : v0.7.1) et `docs/referentiels/style-marculus.md` (P3) ;
  - pour `terrain-style/src/test/resources/jetons/jetons.csv` : une ligne de commentaire en tête qui dit qu'il ne fait foi que pour `reussi` et `alerte`, si les tests le lisent encore. Un tel ajout ne casse pas `./gradlew test` : sinon, ne le fais pas et dis-le.
- **Ne doit pas :** toucher au code, aux assertions de test, au journal ; effacer une décision datée.
- **Sources :** rapport de l'archiviste du bilan n° 3 (`pilotage/journal.md`, ligne « archiviste · bilan 3 ») ; annotations des tags (`git tag -n99 v0.6.1 v0.7.0 v0.7.1`) ; corps de la PR #26 pour la note de version de `terrain-voix`.

## Dépend de / rouge attendu jusqu'à

Dépend de : la PR #32 (journal du bilan) ; branche depuis `main` après sa fusion, ou depuis `origin/pilotage/cycle-6b`. CI rouge attendue : aucune.

## Tests

- `./gradlew test` vert ;
- `grep -n "7:1\|#202F2B\|v0.6.2" docs/brief.md docs/spec.md pilotage/equipe.md` : chaque occurrence restante est sous un bandeau daté « remplacé » ou dans un registre daté ;
- le tableau « Versions » donne v0.6.1, v0.7.0 et v0.7.1, recopiés des annotations de tags.

## Terrain

- **Échantillon :** le tableau « Versions » du README : y lit-on en dix secondes ce qu'apporte chaque version ?

## Ma réponse d'abord

_Rempli par Pascal seul, avant de lire le travail de l'agent._

## Valeur provisoire

Aucune.

## Qui veille, qui tranche

- **Relecteur :** `relecteur`, puis Pascal.
- **Critère d'arrêt de la relecture :** 3 allers-retours.
- **Qui tranche :** Pascal.
