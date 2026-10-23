# Kanban de terrain

> Cinq colonnes (fiche n° 8). Chaque carte porte l'Homme (qui), la Mission (quoi, quand,
> combien) et les Moyens (comment, où, avec qui), et renvoie à sa fiche dans
> `pilotage/missions/`. Elle note aussi depuis quel cycle de `/boucle` elle est dans sa
> colonne ; on change ce numéro chaque fois qu'elle change de colonne.
> **Une carte qui ne bouge pas est souvent la mauvaise nouvelle à dire tout de suite.**

**Dernier cycle :** 1 _(augmenté de 1 à la fin de chaque `/boucle`, même quand elle n'a
rien lancé. Une carte est immobile quand dernier cycle − N ≥ 2.)_

_Modèle de carte :_

```
- [NNN-titre](missions/NNN-titre.md) · depuis le cycle N
  - Homme : qui
  - Mission : quoi, quand, combien
  - Moyens : comment, où, avec qui
```

## Bac à sable

## Validé

- [004-module-contraste](missions/004-module-contraste.md) · depuis le cycle 0
  - Homme : `dev` (sonnet)
  - Mission : module `terrain-style` et `Contraste` ; cycle 2, après 003
  - Moyens : modèle `terrain-carte` ; PR
- [005-tests-jetons](missions/005-tests-jetons.md) · depuis le cycle 0
  - Homme : `testeur` (sonnet)
  - Mission : tests des couples à 7:1 ; cycle 2
  - Moyens : API de la spec ; PR
- [006-generation-schemas](missions/006-generation-schemas.md) · depuis le cycle 0
  - Homme : `dev` (opus), tenu
  - Mission : génération des schémas ; cycle 3, après ADR 0001 et mission 001
  - Moyens : sources MCU ; plan d'abord ; PR
- [007-couleurs-semantiques](missions/007-couleurs-semantiques.md) · depuis le cycle 0
  - Homme : `dev` (sonnet)
  - Mission : réussi et alerte ; cycle 4, après ADR 0002, 002, 006 et le choix de Pascal
  - Moyens : PR
- [008-publication](missions/008-publication.md) · depuis le cycle 0
  - Homme : `dev` (sonnet)
  - Mission : CI sur tags et README ; cycle 4, après ADR 0003
  - Moyens : PR ; le tag est posé par Pascal


## Planifié

- [002-references-couleurs-etat](missions/002-references-couleurs-etat.md) · depuis le cycle 1
  - Homme : `acquereur` (opus)
  - Mission : conventions des couleurs d'état, valeurs sRGB sourcées ; cycle 1
  - Moyens : pages ISO/IEC, MDC ; PR `docs/referentiels/couleurs-etat.md`

## Terminé

- [001-faits-mcu-compose](missions/001-faits-mcu-compose.md) · depuis le cycle 1
  - Homme : `acquereur` (opus)
  - Mission : faits MCU et Compose pour trancher les ADR 0001 et 0002 ; cycle 1
  - Moyens : WebFetch, dépôt MCU, référence `ColorScheme` ; PR #4 fusionnée
- [003-tests-contraste](missions/003-tests-contraste.md) · depuis le cycle 1
  - Homme : `testeur` (sonnet)
  - Mission : tests de `Contraste` et table de deux outils tiers ; cycle 1
  - Moyens : Gradle, color2k 2.0.4 et culori 4.0.2 ; PR #3 fusionnée

## Abandonné

_Avec la raison, en une ligne._

## Livraisons

_Une ligne par cycle, ajoutée sous la précédente à la fin de chaque `/boucle`, et une
par `/bilan` : la livraison que Pascal a retenue, pour qui, et ce qu'elle apporte de plus
que la précédente du même rythme. Un cycle sans livraison neuve livre sa mauvaise
nouvelle, car
« une mauvaise nouvelle est une livraison sur laquelle on apprend » (Pascal) : on
l'inscrit avec ce qu'elle nous apprend._

```
- cycle N · livraison · pour qui · ce qu'elle apporte de plus que la précédente
- cycle N · mauvaise nouvelle : … · pour qui · ce qu'elle nous apprend
- bilan du AAAA-MM-JJ · livraison · pour qui · ce qu'elle apporte de plus que la précédente
- bilan du AAAA-MM-JJ · mauvaise nouvelle : … · pour qui · ce qu'elle nous apprend
```
