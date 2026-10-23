# ADR 0003 — publication de `terrain-style` : tag sur `main` vert, construit par JitPack

- **Date :** 2026-10-23
- **Statut :** proposée
- **Proposée par :** Claude (orchestrateur), sur une remarque du `contradicteur`
- **Tranchée par :** Pascal

## La décision en une phrase

`terrain-style` est publié comme les autres modules, par un tag `vX.Y.Z` posé seulement
sur un commit de `main` dont la CI est verte ; la CI joue aussi les tags ; Surculus épingle
le SHA de ce commit et compte ses 7 jours depuis la première construction JitPack de ce
SHA.

## Contexte

L'issue #1 demande un tag construit par JitPack, et Surculus épingle par SHA de commit au
moins 7 jours après publication (ADR 0008 de Surculus, lettre A). Constats :

- `jitpack.yml` construit sans les tests (`-x test`) ;
- `.github/workflows/ci.yml` ne se déclenche que sur `push` vers `main` et sur les PR,
  pas sur les tags ;
- JitPack construit à la demande, au premier appel, et un artefact peut être reconstruit
  pendant 7 jours avant d'être immuable. Une version par SHA est une construction à part
  du tag (déduction du `contradicteur`, non testée).

Options envisagées :

1. Tag sur `main` vert + CI sur les tags + épinglage par SHA, délai compté depuis la
   construction du SHA.
2. Épinglage par tag + `verification-metadata.xml` (sommes de contrôle Gradle) dans
   Surculus.
3. Tests réactivés dans `jitpack.yml` (build JitPack plus long, risque de délai dépassé).
4. Publication sur Maven Central (signature, compte, délai) : hors de portée pour l'instant.

## Le crible FRANCE

| Lettre | Question | Réponse (une ligne) | Ce que ça coûte | Prêts à payer ? |
|---|---|---|---|---|
| **F**rugale | La plus simple qui tienne ? | Option 1 : une ligne `tags: ['v*']` dans `ci.yml`, une règle de tag | — | _à trancher_ |
| **R**esponsable | Qui fait quoi ? | Pascal pose le tag (porte n° 3) ; la CI le juge ; Surculus déclenche et note la date de construction du SHA | une date à tenir côté Surculus | _à trancher_ |
| **A**nticiper | Si JitPack reconstruit autrement ? | les 7 jours couvrent la fenêtre de reconstruction | — | _à trancher_ |
| **N**ovateur | Toutes les idées ? | quatre options, dont Maven Central | — | — |
| **C**oût | Dépense inutile ? | pas de nouvel outil | une exécution de CI de plus par tag | _à trancher_ |
| **É**cologique | Réutilisation ? | même chaîne que les cinq autres modules | — | _à trancher_ |

## Valeurs sacrifiées

- **Vérification au plus près de l'artefact** : JitPack ne rejoue toujours pas les tests ;
  on se fie à la CI du commit tagué (et au test rejoué par Surculus).

## Sources

- JitPack, documentation (construction à la demande, « immutable after 7 days ») —
  https://docs.jitpack.io/
- `jitpack.yml`, `.github/workflows/ci.yml` du dépôt
- Gradle, dependency verification — https://docs.gradle.org/current/userguide/dependency_verification.html

## Question de contrôle

Cette décision tient-elle nos six valeurs, et que sommes-nous prêts à payer pour cela ?
