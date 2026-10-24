# Mission 015 — gel de réussi #0F8558

> Fiche de mission (fiches n° 9 et 15 de la méthode). Une mission = une phrase = une
> branche = une PR. Tirée de l'essai au soleil de Pascal, fait dans Marculus le 2026-10-24
> (« le test au soleil de marculus valide le style en clair comme en sombre tout est lisible »).

## Mission

**En une phrase (quoi, et pourquoi) :** Geler le vert de départ de réussi `#0F8558` (gardé, sans repli) par une note datée de l'ADR 0002 et retirer la mention « provisoire » du README, de la spec et de `docs/referentiels/couleurs-etat.md`, la spec repassant à « Aucune » question ouverte, parce que l'essai au soleil de Pascal du 2026-10-24 a validé le style.

**Redite par l'agent :** _à compléter par l'agent avant d'agir — s'il n'y arrive pas, il s'arrête._

## Les trois questions (règle de staffing)

| Question | Réponse | Conséquence |
|---|---|---|
| Peut-il redire la mission en une phrase, avec son but ? | oui | — |
| Sait-il comment faire ? (sources, conventions nommées ci-dessous) | oui (README, « Changer le vert », étape 5 ; ADR 0002, notes « Valeur provisoire » et « Spec au gel ») | — |
| A-t-il les moyens ? (outils, données d'essai, tests) | oui | — |

**Temps de la main :** appuyer.

## Ma réponse d'abord

_Rempli par Pascal seul, avant de lire le travail de l'agent : ce qu'il attend de la
mission, et le résultat qu'il prévoit. Une ligne suffit. L'orchestrateur ne le remplit
pas._

## L'Homme

- **Agent :** `archiviste`
- **Modèle :** sonnet
- **Ce qui ne lui est pas demandé :** toucher au code, aux tests ou au code généré ; poser un tag ; trancher une question de valeur (la poser à Pascal).

## Harnais

- **Peut :** modifier `README.md` (ligne `reussi` de la section terrain-style, début de « Changer le vert de départ de `reussi` », paragraphe v0.6.1 de « Publier une version ») ; `docs/spec.md` (« Questions ouvertes » à « Aucune », critère 6 noté fait le 2026-10-24 dans Marculus si le texte s'y prête, sans retirer le critère) ; `docs/adr/0002-couleurs-semantiques.md` (une note datée « Gel, ajoutée le 2026-10-24 », ajout seul) ; `docs/referentiels/couleurs-etat.md` (ligne de statut : une ligne datée ajoutée, sans effacer l'ancienne) ; ouvrir une PR en brouillon. Le kanban, l'équipe et le journal restent à l'orchestrateur.
- **Ne doit pas :** toucher à `main`, effacer une ligne du journal, effacer une décision datée (on ajoute une note « levé le … »), toucher à `docs/brief.md` (arbitrages datés), inventer une valeur ou une source, sortir du périmètre.
- **Sources autorisées :** `README.md`, `docs/`, `pilotage/journal.md` (lecture : lignes du 2026-10-24 sur l'essai au soleil).

## Valeur provisoire (chiffre, réglage)

- **Valeur :** réussi `#0F8558` (reprise de la mission 007, provisoire depuis la mission 013).
- **Mesurée par :** Pascal, essai au soleil dans Marculus, le 2026-10-24 : style validé en clair et en sombre.
- **Gel :** cette mission ; la valeur est gardée, pas de repli `#00843D`.

## Dépend de / rouge attendu jusqu'à

Dépend de : rien (l'essai est fait). CI rouge attendue : aucune.

## Tests

- **Comment saura-t-on que c'est juste :** `grep -n -i provisoire README.md docs/spec.md` ne renvoie plus rien sur `reussi` ; la spec dit « Aucune » sous « Questions ouvertes » ; l'ADR 0002 a une note datée du gel et aucune ligne supprimée (`git diff --stat` : 0 suppression dans l'ADR) ; `docs/brief.md` et le code non touchés ; `./gradlew test` vert.

## Terrain

- **Échantillon que Pascal vérifiera :** la section terrain-style du README : y lit-il en dix secondes que `#0F8558` est définitif et validé au soleil ?

## Qui veille, qui tranche

- **Relecteur :** `relecteur`, puis Pascal.
- **Critère d'arrêt de la relecture :** 3 allers-retours ; au-delà, la décision remonte à
  Pascal.
- **Point de suivi :** à la PR.
- **Qui tranche :** Pascal.

## Comment saura-t-on que ça marche ?

Un développeur de Surculus ou de Groma qui épingle v0.6.0 sait que réussi ne changera pas.
