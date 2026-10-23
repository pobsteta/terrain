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
| [006](missions/006-generation-schemas.md) génération des schémas clair et sombre | `dev` | opus | **tenir** | oui / oui (ADR 0001 acceptée) / oui (mission 001 fusionnée) | 3 |
| [007](missions/007-couleurs-semantiques.md) réussi et alerte | `dev` | sonnet | appuyer | oui / **non** (vert de réussi après mesure HCT) / **non** (002 à fusionner, 006) | 4 |
| [008](missions/008-publication.md) CI sur tags + README | `dev` | sonnet | appuyer | oui / oui (ADR 0003 acceptée) / oui | 4 |
| [009](missions/009-memoire-v0.6.0.md) mémoire de v0.6.0 | `archiviste` | sonnet | appuyer | oui / oui / oui | bilan 1 |
| [010](missions/010-valeur-provisoire.md) valeur provisoire avant mesure | `archiviste` | sonnet | appuyer | oui / oui / oui | 4 |
| [011](missions/011-liste-de-controle.md) liste de contrôle avant « prêt » | `archiviste` | sonnet | appuyer | oui / oui / oui | 4 |
| [012](missions/012-dependances.md) dépendances entre missions | `archiviste` | sonnet | appuyer | oui / oui / oui | 4 |
| [013](missions/013-reussi-provisoire.md) réussi #0F8558 provisoire | `archiviste` | sonnet | appuyer | oui / oui / oui | 4 |

Hors agents : l'**essai au soleil** (critère 6 de la spec) est fait par Pascal, sur le
terrain, après la mission 007, et noté au carnet des écarts ; échéance fixée au bilan n° 1 : 2026-10-30.
