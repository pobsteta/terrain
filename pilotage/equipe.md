# Équipe de terrain

> Produit par l'orchestrateur avec `/imperatrice:mission`, **validé par Pascal** avant
> tout démarrage. Règle de staffing : un agent par mission qui passe les trois
> questions, jamais un agent « par principe ».

- **Statut :** validée
- **Validée par :** Pascal, le 2026-10-23 ; missions 016 à 019 validées le 2026-10-24 ; 020 à 022 lancées le 2026-10-24 sur le cap de Pascal (« c'est la règle et la loi ») ; 023 à 025 décidées au bilan n° 3

## Plafond humain

- **PR ouvertes en même temps, au plus :** 3
- **Rythme de relecture de Pascal :** au fil de l'eau, sans limite par semaine ; plafond de 3 PR ouvertes (Pascal, bilan n° 3 du 2026-10-24 ; remplace « 10 PR par semaine » du bilan n° 2)
- **Version du plugin attendue :** `imperatrice` ≥ 0.6.0

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
| [015](missions/015-gel-reussi.md) gel de réussi `#0F8558` | `archiviste` | sonnet | appuyer | oui / oui / oui | 5 |
| [016](missions/016-referentiel-modele-vosk.md) référentiel du modèle Vosk small-fr-0.22 | `dev` | sonnet | appuyer | oui / oui / oui (réseau requis) | 6 |
| [017](missions/017-tests-installation-modele.md) tests d'acceptation de l'installation du modèle | `testeur` | sonnet | appuyer | oui / oui / oui | 6 |
| [018](missions/018-installation-modele-sure.md) installation sûre du modèle (ADR 0005) | `dev` | opus | **tenir** | oui / oui (API publique : plan d'abord) / après 016 et 017 | 6 |
| [019](missions/019-lire-vocabulaire.md) `lireVocabulaire` | `dev` | sonnet | appuyer | oui / oui / après 016 et 018 | 6 |
| [020](missions/020-referentiel-style-marculus.md) référentiel du style de Marculus | `dev` | sonnet | appuyer | oui / oui / oui | 6 |
| [021](missions/021-tests-style-marculus.md) tests d'égalité au style de Marculus | `testeur` | sonnet | appuyer | oui / oui / après 020 | 6 |
| [022](missions/022-style-marculus.md) terrain-style reprend le style de Marculus (v0.7.1) | `dev` | sonnet | appuyer | oui / oui / après 020 et 021 | 6 |
| [023](missions/023-corrections-bilan-3.md) corrections du bilan n° 3 | `archiviste` | sonnet | appuyer | oui / oui / oui | bilan 3 |
| [024](missions/024-decisions-et-contrat-avant-lancement.md) décisions et contrat avant lancement (plugin) | `archiviste` | sonnet | appuyer | oui / oui / oui | bilan 3 |
| [025](missions/025-controle-des-recopies.md) contrôle des recopies (plugin et terrain) | `archiviste`, `testeur` | sonnet | appuyer | oui / oui / après 024 | bilan 3 |
| [014](missions/014-corrections-bilan-2.md) corrections du bilan n° 2 | `archiviste` | sonnet | appuyer | oui / oui / oui | bilan 2 |

Hors agents : l'**essai au soleil** (critère 6 de la spec) est fait par Pascal, sur le
terrain, après la mission 007, et noté au carnet des écarts ; échéance fixée au bilan n° 1 : 2026-10-30. Fait dans Marculus le 2026-10-24.
