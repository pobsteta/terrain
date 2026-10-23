# Équipe de terrain

> Produit par l'orchestrateur avec `/imperatrice:mission`, **validé par Pascal** avant
> tout démarrage. Règle de staffing : un agent par mission qui passe les trois
> questions, jamais un agent « par principe ».

- **Statut :** validée
- **Validée par :** Pascal, le 2026-10-23

## Plafond humain

- **PR ouvertes en même temps, au plus :** 3
- **Rythme de relecture de Pascal :** _…_

## Le vivier (catalogue, pas une équipe)

| Agent | A / plan | Modèle | Écrit du code ? |
|---|---|---|---|
| `contradicteur` | Abstraction · Polos | opus | non |
| `acquereur` | Acquisition · Ponos | sonnet | non (référentiels et données) |
| `archiviste` | Archivage · Pontos | sonnet | non (docs) |
| `analyste` | Analyse · Ponos | sonnet | non |
| `afficheur` | Affichage · Ponos | sonnet | oui (interface) |
| `dev` | réalisation · Ponos | sonnet | oui |
| `testeur` | réalisation · Ponos | sonnet | oui (tests) |
| `relecteur` | contrôle · C3 | opus | non (lecture seule) |

## Missions

| Mission | Agent | Modèle | Temps de la main | Les 3 questions | Cycle |
|---|---|---|---|---|---|
| [001](missions/001-faits-mcu-compose.md) faits MCU et Compose | `acquereur` | opus | appuyer | oui / oui / oui | 1 |
| [002](missions/002-references-couleurs-etat.md) références des couleurs d'état | `acquereur` | opus | appuyer | oui / oui / oui (normes ISO payantes : dit ce qu'il n'a pas lu) | 1 |
| [003](missions/003-tests-contraste.md) tests de `Contraste` + table tierce | `testeur` | sonnet | appuyer | oui / oui / oui | 1 |
| [004](missions/004-module-contraste.md) module + `Contraste` | `dev` | sonnet | appuyer | oui / oui / après 003 | 2 |
| [005](missions/005-tests-jetons.md) tests des jetons | `testeur` | sonnet | appuyer | oui / oui / oui | 2 |
| [006](missions/006-generation-schemas.md) génération des schémas clair et sombre | `dev` | opus | **tenir** | oui / **non** (ADR 0001) / **non** (mission 001) | 3 |
| [007](missions/007-couleurs-semantiques.md) réussi et alerte | `dev` | sonnet | appuyer | oui / **non** (ADR 0002, choix de Pascal) / **non** (002, 006) | 4 |
| [008](missions/008-publication.md) CI sur tags + README | `dev` | sonnet | appuyer | oui / **non** (ADR 0003) / oui | 4 |

Hors agents : l'**essai au soleil** (critère 6 de la spec) est fait par Pascal, sur le
terrain, après la mission 007, et noté au carnet des écarts.
