# Mission 019 — vocabulaire : « illisible » n'est plus « tout est connu »

> Fiche de mission (fiches n° 9 et 15 de la méthode). Une mission = une phrase = une
> branche = une PR. Tirée de la spec de `terrain-voix` (porte n° 1 du 2026-10-24, ADR 0005).

## Mission

**En une phrase (quoi, et pourquoi) :** Ajouter `VocabulaireVosk.lireVocabulaire(dossier, candidats)`, qui rend `Connus(mots)` ou `Illisible(raison)` en lisant la table de symboles de `graph/Gr.fst` que Vosk utilise, sans toucher à `motsConnus`, jusqu'à faire passer les tests de vocabulaire de la mission 017, pour que Marculus puisse distinguer un vocabulaire illisible d'un vocabulaire entièrement connu.

**Redite par l'agent :** _à compléter par l'agent avant d'agir — s'il n'y arrive pas, il s'arrête._

## Les trois questions (règle de staffing)

| Question | Réponse | Conséquence |
|---|---|---|
| Peut-il redire la mission en une phrase, avec son but ? | oui | — |
| Sait-il comment faire ? (sources, conventions nommées ci-dessous) | oui ; la table à lire (entrée ou sortie) vient de la mission 016 | — |
| A-t-il les moyens ? (outils, données d'essai, tests) | après 016 et 017 | ne part pas avant |

**Temps de la main :** appuyer (ajout d'une fonction, sans changer l'existant).

## Ma réponse d'abord

_Rempli par Pascal seul, avant de lire le travail de l'agent : ce qu'il attend de la
mission, et le résultat qu'il prévoit. Une ligne suffit. L'orchestrateur ne le remplit
pas._

## L'Homme

- **Agent :** `dev`
- **Modèle :** sonnet
- **Ce qui ne lui est pas demandé :** modifier `motsConnus` ou une assertion des tests de 017 ; toucher à `InstallationModele` ; ajouter un repli sur `words.txt` (décision de Pascal du 2026-10-24).

## Harnais

- **Peut :** modifier `terrain-voix/src/main/kotlin/fr/nemeton/terrain/voix/VocabulaireVosk.kt` (ajout seul, plus le type `Vocabulaire`) ; compléter la ligne `terrain-voix` du README ; pousser sur la branche de 018.
- **Ne doit pas :** toucher à `main`, au journal, à `InstallationModele` ; changer la signature de `motsConnus`.
- **Sources autorisées :** spec de `terrain-voix` (« Vocabulaire »), ADR 0005, `docs/referentiels/modele-vosk.md` (mission 016 : tables d'entrée et de sortie), `vosk-api/src/model.cc`, les tests de 017.

## Valeur provisoire (chiffre, réglage)

Aucune.

## Dépend de / rouge attendu jusqu'à

- **Dépend de :** 016 (quelle table lire) et 018 (part de sa branche).
- **Ordre de fusion :** groupée avec 017 et 018 ; c'est la dernière des trois.
- **PR en brouillon jusqu'à :** fin de cette mission et marge fixée (018) : la PR commune sort alors du brouillon.
- **CI rouge attendue :** aucune à la fin de la mission : tout `./gradlew test` est vert.

## Tests

- **Comment saura-t-on que c'est juste :** les tests de vocabulaire de 017 passent sans modification ; les tests existants de `motsConnus` passent toujours ; `./gradlew test` vert.

## Terrain

- **Échantillon que Pascal vérifiera :** aucun directement ; Marculus pourra afficher « vocabulaire illisible » au lieu de tout laisser passer.

## Qui veille, qui tranche

- **Relecteur :** `relecteur`, puis Pascal.
- **Critère d'arrêt de la relecture :** 3 allers-retours ; au-delà, la décision remonte à
  Pascal.
- **Point de suivi :** à la PR.
- **Qui tranche :** Pascal.

## Comment saura-t-on que ça marche ?

Un modèle sans `Gr.fst` donne `Illisible("pas de Gr.fst")`, et le vrai modèle small-fr donne `Connus` avec les mots de la grammaire de Marculus.
