# Mission 011 — liste de contrôle avant « prêt »

> Fiche de mission (fiches n° 9 et 15 de la méthode). Une mission = une phrase = une
> branche = une PR. Tirée du bilan n° 1 du 2026-10-23 (décision de Pascal).

## Mission

**En une phrase (quoi, et pourquoi) :** Écrire, en une page, la liste de contrôle d'auto-relecture que l'acquéreur et le testeur remplissent avant de dire « prêt » (chaque chiffre a sa source et son emplacement, décomptes complets, termes exacts), tirée des 7 renvois du bilan n° 1, parce que c'est la cause B (28 %).

**Redite par l'agent :** _à compléter par l'agent avant d'agir — s'il n'y arrive pas, il s'arrête._

## Les trois questions (règle de staffing)

| Question | Réponse | Conséquence |
|---|---|---|
| Peut-il redire la mission en une phrase, avec son but ? | oui | — |
| Sait-il comment faire ? (sources, conventions nommées ci-dessous) | oui | — |
| A-t-il les moyens ? (outils, données d'essai, tests) | oui | — |

**Temps de la main :** appuyer.

## Ma réponse d'abord

_Rempli par Pascal seul, avant de lire le travail de l'agent : ce qu'il attend de la
mission, et le résultat qu'il prévoit. Une ligne suffit. L'orchestrateur ne le remplit
pas._

## L'Homme

- **Agent :** `archiviste`
- **Modèle :** sonnet
- **Ce qui ne lui est pas demandé :** toucher au code ou aux tests ; trancher une question de valeur (la poser à Pascal).

## Harnais

- **Peut :** ajouter la liste au plugin `imperatrice` (dépôt `/home/pascal/dev/methode-imperatrice`, définitions des agents `acquereur` et `testeur`, ou fichier joint qu'elles citent), sur une branche, version augmentée ; ouvrir une PR sur ce dépôt.
- **Ne doit pas :** toucher à `main`, effacer une ligne du journal, effacer une décision datée (on ajoute une note « amendé » ou « levé le … »), inventer une valeur ou une source, sortir du périmètre.
- **Sources autorisées :** lignes 37, 43, 48, 52, 55, 68 et 93 de `pilotage/journal.md` de terrain, définitions des agents du plugin.

## Tests

- **Comment saura-t-on que c'est juste :** chaque point de la liste renvoie à au moins un des 7 renvois ; une page au plus.

## Terrain

- **Échantillon que Pascal vérifiera :** la liste : aurait-elle évité les trois allers-retours de la mission 002 ?

## Qui veille, qui tranche

- **Relecteur :** `relecteur`, puis Pascal.
- **Critère d'arrêt de la relecture :** 3 allers-retours ; au-delà, la décision remonte à
  Pascal.
- **Point de suivi :** à la PR du plugin.
- **Qui tranche :** Pascal.

## Comment saura-t-on que ça marche ?

Au prochain bilan, moins de « à reprendre » pour sources ou chiffres incomplets.
