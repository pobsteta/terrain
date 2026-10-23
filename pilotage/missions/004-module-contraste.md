# Mission 004 — module-contraste

> Fiche de mission (fiches n° 9 et 15 de la méthode). Une mission = une phrase = une
> branche = une PR. Tirée de `docs/spec.md` (validée le 2026-10-23), issue #1.

## Mission

**En une phrase (quoi, et pourquoi) :** Créer le module Gradle `terrain-style` (Kotlin pur, JVM 17) avec `Contraste.luminance` et `Contraste.rapport` selon WCAG 2.2, pour que les tests de la mission 003 passent et que chaque application puisse rejouer le contraste.

**Redite par l'agent :** _à compléter par l'agent avant d'agir — s'il n'y arrive pas, il s'arrête._

## Les trois questions (règle de staffing)

| Question | Réponse | Conséquence |
|---|---|---|
| Peut-il redire la mission en une phrase, avec son but ? | oui | — |
| Sait-il comment faire ? (sources, conventions nommées ci-dessous) | oui : spec (API, comportement « Contraste »), modèle des autres modules (`terrain-carte/build.gradle.kts`) | — |
| A-t-il les moyens ? (outils, données d'essai, tests) | oui dès la mission 003 fusionnée | sinon planifiée |

**Temps de la main :** appuyer.

## Ma réponse d'abord

_Rempli par Pascal seul, avant de lire le travail de l'agent : ce qu'il attend de la
mission, et le résultat qu'il prévoit. Une ligne suffit. L'orchestrateur ne le remplit
pas._

## L'Homme

- **Agent :** `dev`
- **Modèle :** sonnet
- **Ce qui ne lui est pas demandé :** générer les jetons ; publier un tag.

## Harnais

- **Peut :** ajouter `terrain-style` à `settings.gradle.kts`, son `build.gradle.kts` (publication JitPack comme les autres), `Contraste.kt`, une ligne au README, ouvrir une PR.
- **Ne doit pas :** toucher à `main`, sortir du périmètre, inventer une valeur ou une source, trancher une question de valeur (la poser à Pascal) ; modifier les tests de la mission 003
- **Sources autorisées :** `docs/spec.md`, `CLAUDE.md`, `README.md`, `terrain-carte/` comme modèle.

## Tests

- **Comment saura-t-on que c'est juste :** `./gradlew :terrain-style:test` passe avec les tests de la mission 003 ; `./gradlew test` reste vert.

## Terrain

- **Échantillon que Pascal vérifiera :** `Contraste.rapport(0xFFFFFFFF, 0xFF3E7A40)` dans un essai `publishToMavenLocal`.

## Qui veille, qui tranche

- **Relecteur :** `relecteur`, puis Pascal.
- **Critère d'arrêt de la relecture :** 3 allers-retours ; au-delà, la décision remonte à
  Pascal.
- **Point de suivi :** à la PR.
- **Qui tranche :** Pascal.

## Comment saura-t-on que ça marche ?

Les tests de la mission 003 passent sans avoir été touchés.
