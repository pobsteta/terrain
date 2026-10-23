# Mission 010 — valeur provisoire avant mesure

> Fiche de mission (fiches n° 9 et 15 de la méthode). Une mission = une phrase = une
> branche = une PR. Tirée du bilan n° 1 du 2026-10-23 (décision de Pascal).

## Mission

**En une phrase (quoi, et pourquoi) :** Ajouter au gabarit de fiche de mission du plugin `imperatrice` une ligne « valeur provisoire, mesurée par … avant gel », et y placer la mesure en premier, parce que 8 des 25 incidents du bilan n° 1 viennent d'une valeur fixée avant d'être mesurée (cause A).

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

- **Peut :** modifier `skills/mission/fiche-mission.md` du plugin (dépôt `/home/pascal/dev/methode-imperatrice`, sur une branche, version du plugin augmentée), comme pour la règle « coût complet inconnu » ; ouvrir une PR sur ce dépôt.
- **Ne doit pas :** toucher à `main`, effacer une ligne du journal, effacer une décision datée (on ajoute une note « amendé » ou « levé le … »), inventer une valeur ou une source, sortir du périmètre.
- **Sources autorisées :** Pareto de l'analyste du bilan n° 1 (`pilotage/journal.md`), gabarit actuel du plugin, PR pobsteta/methode-imperatrice#19 (précédent).

## Tests

- **Comment saura-t-on que c'est juste :** le gabarit contient la ligne ; les fiches 009 à 012 de terrain la vérifient sans contradiction ; tests du plugin verts s'il en a.

## Terrain

- **Échantillon que Pascal vérifiera :** la rubrique dans le gabarit : la remplirait-il pour l'essai au soleil ?

## Qui veille, qui tranche

- **Relecteur :** `relecteur`, puis Pascal.
- **Critère d'arrêt de la relecture :** 3 allers-retours ; au-delà, la décision remonte à
  Pascal.
- **Point de suivi :** à la PR du plugin.
- **Qui tranche :** Pascal.

## Comment saura-t-on que ça marche ?

Au prochain bilan, la cause A baisse.
