# Mission 008 — publication

> Fiche de mission (fiches n° 9 et 15 de la méthode). Une mission = une phrase = une
> branche = une PR. Tirée de `docs/spec.md` (validée le 2026-10-23), issue #1.

## Mission

**En une phrase (quoi, et pourquoi) :** Faire jouer la CI sur les tags `v*` avec le contrôle « commit sur `main` » et documenter `terrain-style` dans le README, pour que Pascal pose un tag que Surculus puisse épingler.

**Redite par l'agent :** _à compléter par l'agent avant d'agir — s'il n'y arrive pas, il s'arrête._

## Les trois questions (règle de staffing)

| Question | Réponse | Conséquence |
|---|---|---|
| Peut-il redire la mission en une phrase, avec son but ? | oui | — |
| Sait-il comment faire ? (sources, conventions nommées ci-dessous) | oui une fois l'ADR 0003 tranchée | **non** tant qu'elle est proposée |
| A-t-il les moyens ? (outils, données d'essai, tests) | oui | — |

**Temps de la main :** appuyer.

## Ma réponse d'abord

_Rempli par Pascal seul, avant de lire le travail de l'agent : ce qu'il attend de la
mission, et le résultat qu'il prévoit. Une ligne suffit. L'orchestrateur ne le remplit
pas._

## L'Homme

- **Agent :** `dev`
- **Modèle :** sonnet
- **Ce qui ne lui est pas demandé :** poser le tag (Pascal, porte n° 3) ; toucher `jitpack.yml`.

## Harnais

- **Peut :** modifier `.github/workflows/ci.yml` et `README.md` (module, coordonnées, rôles interdits en texte), ouvrir une PR.
- **Ne doit pas :** toucher à `main`, sortir du périmètre, inventer une valeur ou une source, trancher une question de valeur (la poser à Pascal) ; poser un tag
- **Sources autorisées :** `docs/adr/0003-*.md`, `.github/workflows/ci.yml`, `README.md`, documentation GitHub Actions.

## Tests

- **Comment saura-t-on que c'est juste :** un push de branche déclenche toujours la CI ; un tag sur `main` passe ; un tag hors `main` échoue (essai sur un tag jetable, supprimé ensuite, avec l'accord de Pascal).

## Terrain

- **Échantillon que Pascal vérifiera :** le tag publié et sa page JitPack.

## Qui veille, qui tranche

- **Relecteur :** `relecteur`, puis Pascal.
- **Critère d'arrêt de la relecture :** 3 allers-retours ; au-delà, la décision remonte à
  Pascal.
- **Point de suivi :** à la PR, puis au tag.
- **Qui tranche :** Pascal.

## Comment saura-t-on que ça marche ?

JitPack construit le tag ; Surculus note la date de construction de son SHA.
