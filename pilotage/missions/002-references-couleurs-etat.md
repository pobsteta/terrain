# Mission 002 — references-couleurs-etat

> Fiche de mission (fiches n° 9 et 15 de la méthode). Une mission = une phrase = une
> branche = une PR. Tirée de `docs/spec.md` (validée le 2026-10-23), issue #1.

## Mission

**En une phrase (quoi, et pourquoi) :** Rassembler et comparer les conventions publiées des couleurs d'état (teinte de « réussi », jaune de sécurité et sa couleur de contraste, en sRGB sourcé), pour que Pascal choisisse les valeurs de l'ADR 0002.

**Redite par l'agent :** _à compléter par l'agent avant d'agir — s'il n'y arrive pas, il s'arrête._

## Les trois questions (règle de staffing)

| Question | Réponse | Conséquence |
|---|---|---|
| Peut-il redire la mission en une phrase, avec son but ? | oui | — |
| Sait-il comment faire ? (sources, conventions nommées ci-dessous) | oui : besoin 4 de la spec, ADR 0002 (ISO 3864-1, ISO 3864-4, ISO 22324, IEC 60073, MDC « custom colors ») | — |
| A-t-il les moyens ? (outils, données d'essai, tests) | oui pour les sources publiques ; les normes ISO sont payantes : l'agent dit ce qu'il n'a pas pu lire | — |

**Temps de la main :** appuyer — la mission prépare un choix de valeur de Pascal.

## Ma réponse d'abord

_Rempli par Pascal seul, avant de lire le travail de l'agent : ce qu'il attend de la
mission, et le résultat qu'il prévoit. Une ligne suffit. L'orchestrateur ne le remplit
pas._

## L'Homme

- **Agent :** `acquereur`
- **Modèle :** opus (justesse des données)
- **Ce qui ne lui est pas demandé :** choisir la teinte ou le jaune ; calculer des teintes HCT ou des contrastes (la génération, mission 007, les mesure).

## Harnais

- **Peut :** écrire `docs/referentiels/couleurs-etat.md`, ouvrir une PR.
- **Ne doit pas :** toucher à `main`, sortir du périmètre, inventer une valeur ou une source, trancher une question de valeur (la poser à Pascal)
- **Sources autorisées :** `docs/spec.md` (besoin 4), `docs/adr/0002-*.md`, pages publiques ISO/IEC, aperçus, MDC-Android `Color.md`, sources secondaires signalées comme telles.

## Tests

- **Comment saura-t-on que c'est juste :** au moins deux sources comparées par valeur ; chaque valeur sRGB avec sa source et sa méthode de conversion (illuminant, espace) ; ce qui n'a pas pu être vérifié, listé.

## Terrain

- **Échantillon que Pascal vérifiera :** les candidats « jaune clair + noir » et « vert de réussite », vus côte à côte sur un écran.

## Qui veille, qui tranche

- **Relecteur :** `relecteur`, puis Pascal.
- **Critère d'arrêt de la relecture :** 3 allers-retours ; au-delà, la décision remonte à
  Pascal.
- **Point de suivi :** à la PR, puis au moment où Pascal choisit les valeurs de l'ADR 0002.
- **Qui tranche :** Pascal.

## Comment saura-t-on que ça marche ?

Pascal choisit la teinte de réussi et le jaune en une lecture, sans nouvelle recherche.
