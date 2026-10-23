# ADR 0003 — publication de `terrain-style` : tag sur `main` vert, construit par JitPack

- **Date :** 2026-10-23
- **Statut :** acceptée par Pascal le 2026-10-23 (« Prêts à payer ? » : inconnu)
- **Proposée par :** Claude (orchestrateur), sur une remarque du `contradicteur`
- **Tranchée par :** Pascal

## La décision en une phrase

`terrain-style` est publié comme les autres modules, par un tag `vX.Y.Z` posé seulement
sur un commit de `main`, ce que la CI contrôle ; Surculus épingle le **SHA court** de ce
commit, compte ses 7 jours depuis la première construction JitPack de ce SHA, et fige la
somme de contrôle de l'artefact.

## Contexte

L'issue #1 demande un tag construit par JitPack, et Surculus épingle par SHA de commit au
moins 7 jours après publication (ADR 0008 de Surculus, lettre A). Constats :

- `jitpack.yml` construit sans les tests (`-x test`) ; JitPack construit à la première
  demande, CI verte ou non ;
- `.github/workflows/ci.yml` ne se déclenche que sur `push` vers `main` et sur les PR ;
  un commit de `main` a donc déjà eu sa CI, mais rien n'empêche de poser un tag hors de
  `main` ;
- JitPack : « Public repository artifacts on JitPack are immutable after 7 days of
  publishing », « Within the first 7 days they can be re-built » ; pour un commit, la doc
  dit de prendre « the short commit id as the version ».

Options envisagées :

1. Tag sur `main` contrôlé par la CI + épinglage par SHA court + somme de contrôle figée
   dans Surculus (`verification-metadata.xml`).
2. Épinglage par tag seul + `verification-metadata.xml`.
3. Tests réactivés dans `jitpack.yml` (build JitPack plus long).
4. Publication sur Maven Central (signature, compte, délai) : hors de portée pour l'instant.

Mise en œuvre de l'option 1 : `on: push: tags: ['v*']` dans `ci.yml`, **à côté** de
`branches: [main]` (sans `branches`, les pushes de branche ne déclencheraient plus rien) ;
une étape qui échoue si `git merge-base --is-ancestor "$GITHUB_SHA" origin/main` est faux.

## Le crible FRANCE

| Lettre | Question | Réponse (une ligne) | Ce que ça coûte | Prêts à payer ? |
|---|---|---|---|---|
| **F**rugale | La plus simple qui tienne ? | Option 1 : deux lignes de déclencheur et une étape dans `ci.yml` | — | inconnu |
| **R**esponsable | Qui fait quoi ? | Pascal pose le tag (porte n° 3) ; la CI vérifie qu'il est sur `main` ; Surculus déclenche le SHA, note sa date de construction et fige la somme | une date et un fichier à tenir côté Surculus | inconnu |
| **A**nticiper | Si JitPack reconstruit ou tombe en panne ? | la somme figée détecte une reconstruction dans les 7 jours ; JitPack garde les artefacts même si le dépôt disparaît | une panne de JitPack bloque la construction des applications | inconnu |
| **N**ovateur | Toutes les idées ? | quatre options, dont Maven Central | — | — |
| **C**oût | Dépense inutile ? | pas de nouvel outil | une exécution de CI de plus par tag | inconnu |
| **É**cologique | Réutilisation ? | même chaîne que les cinq autres modules | — | inconnu |

## Valeurs sacrifiées

- **Vérification au plus près de l'artefact** : JitPack ne rejoue toujours pas les tests ;
  on se fie à la CI du commit tagué et au test rejoué par Surculus.
- **Indépendance d'un tiers** : JitPack est un service gratuit, sans engagement de
  service.

## Sources

- JitPack, documentation (construction à la demande, immutabilité après 7 jours, SHA
  court, conservation après suppression du dépôt) — https://docs.jitpack.io/
- GitHub Actions, syntaxe des déclencheurs `branches` / `tags` —
  https://docs.github.com/en/actions/writing-workflows/workflow-syntax-for-github-actions
- `jitpack.yml`, `.github/workflows/ci.yml` du dépôt
- Gradle, dependency verification — https://docs.gradle.org/current/userguide/dependency_verification.html

## Question de contrôle

Cette décision tient-elle nos six valeurs, et que sommes-nous prêts à payer pour cela ?
