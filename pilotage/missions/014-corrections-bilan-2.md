# Mission 014 — corrections du bilan n° 2

> Fiche de mission (fiches n° 9 et 15 de la méthode). Une mission = une phrase = une
> branche = une PR. Tirée du bilan n° 2 du 2026-10-23 (décision de Pascal).

## Mission

**En une phrase (quoi, et pourquoi) :** Appliquer à terrain les corrections P1 à P3 de l'archiviste du bilan n° 2 (spec, README, référentiels, lien Surculus, `.gitkeep`), pour qu'un successeur trouve tout juste en une minute en attendant l'essai au soleil.

**Redite par l'agent :** _à compléter par l'agent avant d'agir — s'il n'y arrive pas, il s'arrête._

## Les trois questions (règle de staffing)

| Question | Réponse | Conséquence |
|---|---|---|
| Peut-il redire la mission en une phrase, avec son but ? | oui | — |
| Sait-il comment faire ? (sources, conventions nommées ci-dessous) | oui | — |
| A-t-il les moyens ? (outils, données d'essai, tests) | oui | — |

**Temps de la main :** appuyer (lâcher possible à la prochaine mission de documents, bilan n° 2).

## Ma réponse d'abord

_Rempli par Pascal seul, avant de lire le travail de l'agent : ce qu'il attend de la
mission, et le résultat qu'il prévoit. Une ligne suffit. L'orchestrateur ne le remplit
pas._

## L'Homme

- **Agent :** `archiviste`
- **Modèle :** sonnet
- **Ce qui ne lui est pas demandé :** toucher au code ou aux tests ; trancher une question de valeur (la poser à Pascal).

## Harnais

- **Peut :** modifier `docs/spec.md` (« Questions ouvertes » : une, l'essai au soleil), `README.md` (exemple `NmeaParser` remonté sous « Utiliser » ; renvoi de « Publier une version » vers la marche à suivre de la v0.6.1 ; lien vers le dépôt Surculus), `docs/brief.md` (lien Surculus seulement, sans toucher aux arbitrages datés), `docs/referentiels/couleurs-etat.md` et `mcu-compose.md` (ligne de statut datée, ajout seul), supprimer `docs/adr/.gitkeep` ; ouvrir une PR en brouillon. Le kanban et `pilotage/` restent à l'orchestrateur.
- **Ne doit pas :** toucher à `main`, effacer une ligne du journal, effacer une décision datée (on ajoute une note « amendé » ou « levé le … »), inventer une valeur ou une source, sortir du périmètre.
- **Sources autorisées :** rapport de l'archiviste du bilan n° 2 (`pilotage/journal.md`), `README.md`, `docs/`, `https://github.com/pobsteta/surculus` (à vérifier avant d'écrire le lien ; s'il n'existe pas ou est privé, le dire sans inventer).

## Valeur provisoire (chiffre, réglage)

Reprend `#0F8558` sans le changer (provisoire jusqu'à l'essai au soleil du 2026-10-30, ADR 0002) ; n'en fixe aucune.

## Dépend de / rouge attendu jusqu'à

Dépend de : rien. CI rouge attendue : aucune.

## Tests

- **Comment saura-t-on que c'est juste :** aucune phrase de `docs/spec.md` ne dit « aucune question ouverte » ; le lien Surculus répond (ou l'absence est dite) ; aucune ligne d'arbitrage daté ni d'ADR supprimée ; `./gradlew test` vert.

## Terrain

- **Échantillon que Pascal vérifiera :** le README d'un bout à l'autre : les sections sont-elles dans l'ordre où un développeur d'application les cherche ?

## Qui veille, qui tranche

- **Relecteur :** `relecteur`, puis Pascal.
- **Critère d'arrêt de la relecture :** 3 allers-retours ; au-delà, la décision remonte à
  Pascal.
- **Point de suivi :** à la PR.
- **Qui tranche :** Pascal.

## Comment saura-t-on que ça marche ?

Au bilan suivant, l'archiviste ne trouve plus de correction P3 restante.
