# Mission 013 — réussi #0F8558 provisoire

> Fiche de mission (fiches n° 9 et 15 de la méthode). Une mission = une phrase = une
> branche = une PR. Tirée du cycle 4 (relecture de la mission 010, décision de Pascal du 2026-10-23).

## Mission

**En une phrase (quoi, et pourquoi) :** Marquer « provisoire jusqu'à l'essai au soleil du 2026-10-30 » le vert de départ de réussi #0F8558 dans le README, l'ADR 0002 (note datée) et la spec, parce qu'il attend la mesure de Pascal et que la rubrique « Valeur provisoire » du plugin 0.5.0 l'exige (décision de Pascal du 2026-10-23).

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

- **Peut :** modifier `README.md` (section terrain-style et marche à suivre du repli), `docs/adr/0002-couleurs-semantiques.md` (note datée, ajout seul), `docs/spec.md` ; ouvrir une PR en brouillon.
- **Ne doit pas :** toucher à `main`, effacer une ligne du journal, effacer une décision datée (on ajoute une note « amendé » ou « levé le … »), inventer une valeur ou une source, sortir du périmètre.
- **Sources autorisées :** `README.md`, `docs/`, `pilotage/journal.md` (lecture : décisions de la mission 007 et du bilan n° 1), gabarit de fiche du plugin 0.5.0 (rubrique « Valeur provisoire »).

## Valeur provisoire (chiffre, réglage)

- **Valeur :** réussi `#0F8558` (reprise, fixée par la mission 007).
- **Mesurée par :** Pascal, essai au soleil, d'ici le 2026-10-30 (mesure qui revient à un autre : la valeur reste provisoire jusqu'à cette date).
- **Gel :** après l'essai, par une note datée dans l'ADR 0002 (garder `#0F8558` ou passer au repli `#00843D`).

## Tests

- **Comment saura-t-on que c'est juste :** chaque mention de `#0F8558` dans `README.md` et `docs/spec.md` est marquée provisoire ou renvoie à un passage qui l'est (grep dans la PR) ; aucune ligne d'ADR supprimée ; `./gradlew test` vert (aucun code touché). Le code généré (`StyleTerrain.kt`) n'est pas touché : il sera marqué à la prochaine génération.

## Terrain

- **Échantillon que Pascal vérifiera :** la section terrain-style du README : y voit-il en dix secondes que réussi attend son essai au soleil ?

## Qui veille, qui tranche

- **Relecteur :** `relecteur`, puis Pascal.
- **Critère d'arrêt de la relecture :** 3 allers-retours ; au-delà, la décision remonte à
  Pascal.
- **Point de suivi :** à la PR.
- **Qui tranche :** Pascal.

## Comment saura-t-on que ça marche ?

Un développeur de Surculus qui épingle v0.6.0 sait que réussi peut changer en v0.6.1.
