# Mission 006 — generation-schemas

> Fiche de mission (fiches n° 9 et 15 de la méthode). Une mission = une phrase = une
> branche = une PR. Tirée de `docs/spec.md` (validée le 2026-10-23), issue #1.

## Mission

**En une phrase (quoi, et pourquoi) :** Écrire le script qui compile les sources de Material Color Utilities recopiées au commit inscrit et génère les schémas clair et sombre (#374742, FIDELITY, SPEC_2021, contraste 1,0), figés en Kotlin, avec la table des jetons et le rejeu en CI, pour que les trois applications reçoivent les mêmes rôles.

**Redite par l'agent :** _à compléter par l'agent avant d'agir — s'il n'y arrive pas, il s'arrête._

## Les trois questions (règle de staffing)

| Question | Réponse | Conséquence |
|---|---|---|
| Peut-il redire la mission en une phrase, avec son but ? | oui | — |
| Sait-il comment faire ? (sources, conventions nommées ci-dessous) | oui une fois l'ADR 0001 tranchée | **non** tant que l'ADR 0001 est proposée |
| A-t-il les moyens ? (outils, données d'essai, tests) | **non** tant que la mission 001 (commit, sources, version de Kotlin) n'est pas fusionnée | planifiée |

**Temps de la main :** tenir — la mission fixe la chaîne de génération pour la durée du module : l'agent rend d'abord un plan (dont la mesure de parité Java/Kotlin), relu avant exécution.

## Ma réponse d'abord

_Rempli par Pascal seul, avant de lire le travail de l'agent : ce qu'il attend de la
mission, et le résultat qu'il prévoit. Une ligne suffit. L'orchestrateur ne le remplit
pas._

## L'Homme

- **Agent :** `dev`
- **Modèle :** opus (architecture)
- **Ce qui ne lui est pas demandé :** les couleurs sémantiques réussi et alerte (mission 007) ; retoucher une valeur à la main.

## Harnais

- **Peut :** recopier les sources MCU dans `terrain-style/generation/mcu/` (LICENSE, en-têtes, fichiers modifiés marqués), tâche `:terrain-style:generer`, `Schema`, `StyleTerrain`, table des jetons, étape CI `generer` + `git diff --exit-code`, ouvrir une PR.
- **Ne doit pas :** toucher à `main`, sortir du périmètre, inventer une valeur ou une source, trancher une question de valeur (la poser à Pascal) ; retoucher un jeton ; modifier les tests de la mission 005 ; faire dépendre le module publié de MCU
- **Sources autorisées :** `docs/spec.md`, `docs/adr/0001-*.md`, `docs/referentiels/mcu-compose.md` (mission 001), sources MCU au commit inscrit.

## Tests

- **Comment saura-t-on que c'est juste :** tests de la mission 005 ; `scheme.specVersion == SPEC_2021` vérifié ; rejeu identique à l'octet en CI ; les couples en échec sont listés et leurs rôles documentés « interdit en texte », sans retouche.

## Terrain

- **Échantillon que Pascal vérifiera :** les deux schémas affichés en nuancier (clair et sombre) à côté de VertMarculus.

## Qui veille, qui tranche

- **Relecteur :** `relecteur`, puis Pascal.
- **Critère d'arrêt de la relecture :** 3 allers-retours ; au-delà, la décision remonte à
  Pascal.
- **Point de suivi :** au plan, puis à la PR.
- **Qui tranche :** Pascal.

## Comment saura-t-on que ça marche ?

La CI régénère et ne trouve aucune différence ; Pascal reconnaît (ou non) VertMarculus dans le nuancier.

## Précisions de l'orchestrateur (2026-10-23, décisions de Pascal)

- **Branche** : partir de `mission/005-tests-jetons` (rebasée sur `main` après la mission
  004) ; la PR 006 fusionne avec 005 et 007, ensemble, vertes.
- **Périmètre de l'API** : 006 fournit `StyleTerrain`, `Schema` (48 rôles et
  `interditsEnTexte`) et des valeurs provisoires lisibles pour `reussi` et `alerte` ne sont
  **pas** à inventer : `Semantique` et ses valeurs arrivent avec 007. Si les tests de 005 ne
  peuvent pas compiler sans elles, le dire au plan.
- **`Schema` refuse** toute valeur hors `0xFF000000..0xFFFFFFFF` par
  `IllegalArgumentException`.
- **README** : chaque rôle déclaré « interdit en texte » y est écrit entre accents graves.
- **Marges lisibles** : ajouter `testLogging { showStandardStreams = true }` (ou équivalent)
  au `build.gradle.kts` du module, pour que les lignes `MARGE` sortent dans la console.
