# Mission 009 — mémoire de v0.6.0

> Fiche de mission (fiches n° 9 et 15 de la méthode). Une mission = une phrase = une
> branche = une PR. Tirée du bilan n° 1 du 2026-10-23 (décision de Pascal).

## Mission

**En une phrase (quoi, et pourquoi) :** Appliquer les corrections P1 et P2 de l'archiviste du bilan n° 1 (documents périmés, marche à suivre du repli #00843D, prérequis des tests), pour qu'un successeur reprenne terrain-style sans nous.

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

- **Peut :** modifier `docs/brief.md` (statut, ligne 42 : variante `CONTENT` ; une phrase en tête des arbitrages : les sections postérieures remplacent les antérieures), `docs/spec.md` (statut des ADR 0001 à 0004, mécanisme `errorPalette` vérifié, estimation remplacée par le constat, questions ouvertes tranchées), les ADR 0001 et 0002 par **notes datées** (« amendé », « levé le 2026-10-23 ») sans effacer, en écrivant dans l'ADR 0002 que `#00843D` sort de la région verte d'ISO 3864-4 (`docs/referentiels/couleurs-etat.md`) ; `README.md`, section « Développer » : marche à suivre pour changer la couleur de départ de `reussi` (fichiers à toucher, commandes, tag posé par Pascal), `testGeneration` hors de `.claude/test-command`, prérequis Node et Python de la table de référence ; ouvrir une PR.
- **Ne doit pas :** toucher à `main`, effacer une ligne du journal, effacer une décision datée (on ajoute une note « amendé » ou « levé le … »), inventer une valeur ou une source, sortir du périmètre.
- **Sources autorisées :** rapport de l'archiviste du bilan n° 1 (repris dans `pilotage/journal.md`), `docs/`, `README.md`, `terrain-style/generation/` (lecture), `.claude/test-command`, `pilotage/journal.md` (lecture).

## Tests

- **Comment saura-t-on que c'est juste :** `./gradlew test` et `./gradlew :terrain-style:testGeneration` restent verts (aucun code touché) ; plus aucune phrase de `docs/brief.md` hors arbitrages datés, de `docs/spec.md` ni des décisions en une phrase des ADR ne contredit `CONTENT`, `#E2AA00` ou `#0F8558` (`grep` fourni dans la PR).

## Terrain

- **Échantillon que Pascal vérifiera :** la section du README sur le repli `#00843D` : peut-il la suivre sans aide ?

## Qui veille, qui tranche

- **Relecteur :** `relecteur`, puis Pascal.
- **Critère d'arrêt de la relecture :** 3 allers-retours ; au-delà, la décision remonte à
  Pascal.
- **Point de suivi :** à la PR.
- **Qui tranche :** Pascal.

## Comment saura-t-on que ça marche ?

Un successeur lit le README et sait faire une v0.6.1 en changeant le vert de réussi.
