# Mission 007 — couleurs-semantiques

> Fiche de mission (fiches n° 9 et 15 de la méthode). Une mission = une phrase = une
> branche = une PR. Tirée de `docs/spec.md` (validée le 2026-10-23), issue #1.

## Mission

**En une phrase (quoi, et pourquoi) :** Générer « réussi » par la palette d'erreur de MCU et figer « alerte » (jaune clair et noir de la convention de sécurité), en clair et en sombre, pour que les trois applications partagent leurs états communs.

**Redite par l'agent :** _à compléter par l'agent avant d'agir — s'il n'y arrive pas, il s'arrête._

## Les trois questions (règle de staffing)

| Question | Réponse | Conséquence |
|---|---|---|
| Peut-il redire la mission en une phrase, avec son but ? | oui | — |
| Sait-il comment faire ? (sources, conventions nommées ci-dessous) | **non** tant que l'ADR 0002 n'est pas tranchée et que Pascal n'a pas choisi la teinte de réussi et le jaune | planifiée |
| A-t-il les moyens ? (outils, données d'essai, tests) | **non** tant que les missions 002 et 006 ne sont pas fusionnées | planifiée |

**Temps de la main :** appuyer — si `errorPalette` n'est pas accessible, l'agent s'arrête et la question remonte à Pascal.

## Ma réponse d'abord

_Rempli par Pascal seul, avant de lire le travail de l'agent : ce qu'il attend de la
mission, et le résultat qu'il prévoit. Une ligne suffit. L'orchestrateur ne le remplit
pas._

## L'Homme

- **Agent :** `dev`
- **Modèle :** sonnet
- **Ce qui ne lui est pas demandé :** choisir une teinte ; harmoniser sans décision de Pascal.

## Harnais

- **Peut :** étendre le script de génération et `Schema` (`reussi`, `alerte`, `interditsEnTexte`), mesurer les teintes HCT avant/après harmonisation, ouvrir une PR.
- **Ne doit pas :** toucher à `main`, sortir du périmètre, inventer une valeur ou une source, trancher une question de valeur (la poser à Pascal) ; retoucher une valeur générée
- **Sources autorisées :** `docs/spec.md`, `docs/adr/0002-*.md`, `docs/referentiels/couleurs-etat.md` (mission 002).

## Tests

- **Comment saura-t-on que c'est juste :** tests de la mission 005 sur `reussi` et `alerte` ; écart HCT de `reussi` à `primary` affiché.

## Terrain

- **Échantillon que Pascal vérifiera :** réussi, alerte et erreur côte à côte, en clair, au soleil.

## Qui veille, qui tranche

- **Relecteur :** `relecteur`, puis Pascal.
- **Critère d'arrêt de la relecture :** 3 allers-retours ; au-delà, la décision remonte à
  Pascal.
- **Point de suivi :** à la PR.
- **Qui tranche :** Pascal.

## Comment saura-t-on que ça marche ?

Pascal distingue réussi de `primary` et reconnaît le jaune d'alerte sur l'écran.
