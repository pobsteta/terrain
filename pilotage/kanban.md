# Kanban de terrain

> Cinq colonnes (fiche n° 8). Chaque carte porte l'Homme (qui), la Mission (quoi, quand,
> combien) et les Moyens (comment, où, avec qui), et renvoie à sa fiche dans
> `pilotage/missions/`. Elle note aussi depuis quel cycle de `/boucle` elle est dans sa
> colonne ; on change ce numéro chaque fois qu'elle change de colonne.
> **Une carte qui ne bouge pas est souvent la mauvaise nouvelle à dire tout de suite.**

**Dernier cycle :** 6 _(augmenté de 1 à la fin de chaque `/boucle`, même quand elle n'a
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

## Planifié

## Terminé

- [021-tests-style-marculus](missions/021-tests-style-marculus.md) · depuis le cycle 6
  - Homme : `testeur` (sonnet)
  - Mission : tests d'égalité au style de Marculus ; 7:1 devient informatif
  - Moyens : après 020 ; branche de 020
  - Livré : PR #30 fusionnée via #31 (409 tests)
- [022-style-marculus](missions/022-style-marculus.md) · depuis le cycle 6
  - Homme : `dev` (sonnet)
  - Mission : `terrain-style` = style de Marculus, nuancier, README ; tag v0.7.1 après le « c'est vert » de Pascal
  - Moyens : après 021 ; branche de 021
  - Livré : PR #31 fusionnée ; **tag v0.7.1** (nuancier validé par Pascal : « oui c'est le vert de Marculus »)
- [017-tests-installation-modele](missions/017-tests-installation-modele.md) · depuis le cycle 6
  - Homme : `testeur` (sonnet)
  - Mission : jeux d'essai figés et tests d'acceptation 1 à 8 de la spec `terrain-voix`
  - Moyens : spec, ADR 0005, Info-ZIP, `sha256sum` ; PR en brouillon, CI rouge annoncée jusqu'à 019
  - Livré : PR #26 fusionnée (3 relectures + complément)
- [018-installation-modele-sure](missions/018-installation-modele-sure.md) · depuis le cycle 6
  - Homme : `dev` (opus), tenu
  - Mission : réécrire `InstallationModele` (ADR 0005) ; plan d'abord, marge proposée puis fixée par Pascal
  - Moyens : après 016 et 017 ; branche de 017
  - Livré : PR #26 fusionnée (1 relecture + renvoi 1/3 ; renvoi non relu, fusion sur ordre de Pascal)
- [019-lire-vocabulaire](missions/019-lire-vocabulaire.md) · depuis le cycle 6
  - Homme : `dev` (sonnet)
  - Mission : ajouter `lireVocabulaire` (connus / illisible)
  - Moyens : après 016 et 018 ; branche de 018
  - Livré : PR #26 fusionnée (intégrée par l'orchestrateur, non relue à part)
- [020-referentiel-style-marculus](missions/020-referentiel-style-marculus.md) · depuis le cycle 6
  - Homme : `dev` (sonnet)
  - Mission : relever le style de Marculus v0.65.0 et les défauts de Material 3 (BOM 2026.02.01)
  - Moyens : dépôt Marculus, jars Material 3 ; référentiel et CSV ; PR
  - Livré : PR #29 fusionnée (1 relecture)
- [016-referentiel-modele-vosk](missions/016-referentiel-modele-vosk.md) · depuis le cycle 6
  - Homme : `dev` (sonnet)
  - Mission : relever le vrai modèle small-fr-0.22 (fichiers, tailles, empreinte, tables de `Gr.fst`)
  - Moyens : réseau, `sha256sum`, `zipinfo` ; `docs/referentiels/modele-vosk.md` ; PR
  - Livré : PR #27 fusionnée (2 allers-retours) ; `docs/referentiels/modele-vosk.md`
- [015-gel-reussi](missions/015-gel-reussi.md) · depuis le cycle 5
  - Homme : `archiviste` (sonnet)
  - Mission : geler `#0F8558` après l'essai au soleil (note datée de l'ADR 0002, mention « provisoire » retirée, spec à « Aucune ») ; lancée le 2026-10-24
  - Moyens : README, spec, ADR 0002, `couleurs-etat.md` ; liste de contrôle jointe ; PR
  - Livré : PR #24 fusionnée (1 aller-retour de relecture)
- Essai au soleil (critère 6 de la spec) · depuis le cycle 5
  - Homme : Pascal, hors agents
  - Mission : six bandeaux d'état en clair, `tertiary` contre `error` en sombre, bord de la pastille d'alerte ; d'ici le 2026-10-30
  - Dépend de : l'intégration de terrain-style dans Marculus (faite par Pascal, hors de ce dépôt) ; l'essai se fait dans Marculus
  - Moyens : même écran, plein soleil, fiche en thème clair ; repli `#00843D` (v0.6.1) si réussi se confond avec `primary`
  - Résultat : une ligne `écart` au carnet des écarts de `pilotage/journal.md`, puis une note datée de gel dans l'ADR 0002 (même PR que la levée de la mention « provisoire », marche à suivre du README)
  - Livré : fait par Pascal dans Marculus le 2026-10-24 ; tout est lisible, en clair comme en sombre ; `#0F8558` gardé (gel : mission 015)
  - Repli `#00843D` : levé au gel le 2026-10-24 (décision de Pascal) ; tout changement de vert demande une nouvelle décision
- [014-corrections-bilan-2](missions/014-corrections-bilan-2.md) · depuis le cycle 4
  - Homme : `archiviste` (sonnet)
  - Mission : corrections P1 à P3 du bilan n° 2 ; lancée au bilan n° 2
  - Moyens : rapport de l'archiviste ; liste de contrôle jointe ; PR
  - Livré : PR #20 fusionnée (1 retouche sur décisions de Pascal)
- [013-reussi-provisoire](missions/013-reussi-provisoire.md) · depuis le cycle 4
  - Homme : `archiviste` (sonnet)
  - Mission : marquer #0F8558 provisoire jusqu'à l'essai au soleil ; lancée au cycle 4
  - Moyens : README, ADR 0002, spec ; PR
  - Livré : PR #17 fusionnée
- [012-dependances](missions/012-dependances.md) · depuis le cycle 4
  - Homme : `archiviste` (sonnet)
  - Mission : rubrique « dépend de / rouge attendu jusqu'à » (cause C) ; lancée au cycle 4, après la fusion de la 010
  - Moyens : dépôt du plugin ; PR
  - Livré : PR pobsteta/methode-imperatrice#22 fusionnée (plugin v0.6.0, 2 allers-retours)
- [011-liste-de-controle](missions/011-liste-de-controle.md) · depuis le cycle 4
  - Homme : `archiviste` (sonnet)
  - Mission : liste de contrôle avant « prêt » (cause B) ; lancée au cycle 4
  - Moyens : 7 renvois du journal ; PR sur le plugin
  - Livré : contenu fusionné via la PR #20 du plugin (v0.5.0), PR #21 fermée
- [010-valeur-provisoire](missions/010-valeur-provisoire.md) · depuis le cycle 4
  - Homme : `archiviste` (sonnet)
  - Mission : ligne « valeur provisoire » au gabarit de fiche du plugin (cause A) ; lancée au cycle 4
  - Moyens : dépôt du plugin ; PR
  - Livré : PR pobsteta/methode-imperatrice#20 fusionnée (plugin v0.5.0)
- [009-memoire-v0.6.0](missions/009-memoire-v0.6.0.md) · depuis le cycle 3
  - Homme : `archiviste` (sonnet)
  - Mission : corrections P1 et P2 du bilan n° 1 ; lancée au bilan du 2026-10-23
  - Moyens : rapport de l'archiviste ; PR
  - Livré : PR #16 fusionnée (1 aller-retour de relecture)
- [005-tests-jetons](missions/005-tests-jetons.md) · depuis le cycle 3
  - Homme : `testeur` (sonnet)
  - Mission : tests des couples à 7:1 ; cycle 2
  - Moyens : API de la spec ; PR #8 en brouillon, fusion groupée avec 006 et 007
  - Livré : PR #8 fusionnée (tag v0.6.0)
- [006-generation-schemas](missions/006-generation-schemas.md) · depuis le cycle 3
  - Homme : `dev` (opus), tenu
  - Mission : génération des schémas ; cycle 3 (ADR 0001 acceptée, 001 fusionnée), depuis la branche 005
  - Moyens : sources MCU ; plan d'abord ; PR
  - Livré : PR #11 fusionnée (tag v0.6.0)
- [007-couleurs-semantiques](missions/007-couleurs-semantiques.md) · depuis le cycle 3
  - Homme : `dev` (sonnet)
  - Mission : réussi et alerte ; cycle 4, après ADR 0002, 002, 006 et le choix de Pascal
  - Moyens : PR
  - Livré : PR #12 fusionnée (tag v0.6.0)
- [008-publication](missions/008-publication.md) · depuis le cycle 3
  - Homme : `dev` (sonnet)
  - Mission : CI sur tags et README ; cycle 3 (ADR 0003 acceptée)
  - Moyens : PR ; le tag est posé par Pascal
  - Livré : PR #9 fusionnée (tag v0.6.0)

- [002-references-couleurs-etat](missions/002-references-couleurs-etat.md) · depuis le cycle 2
  - Homme : `acquereur` (opus)
  - Mission : conventions des couleurs d'état, valeurs sRGB sourcées ; cycle 1
  - Moyens : pages ISO/IEC, MDC ; PR #5 fusionnée
- [004-module-contraste](missions/004-module-contraste.md) · depuis le cycle 2
  - Homme : `dev` (sonnet)
  - Mission : module `terrain-style` et `Contraste` ; cycle 2, après 003
  - Moyens : modèle `terrain-carte` ; PR #7 fusionnée

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
- cycle 1 · table de référence WCAG `terrain-style/src/test/resources/contraste/reference.csv` (200 couples, deux outils tiers, rejouable à l'octet) · pour les développeurs de Surculus · première livraison : de quoi tester leur propre palette d'état dès maintenant
- cycle 2 · module `terrain-style` et `Contraste.rapport` (WCAG 2.2), publiable par JitPack au premier tag · pour les développeurs de Marculus, Groma et Surculus · une fonction à appeler au lieu d'une table à rejouer
- cycle 3 · `terrain-style` complet, tag v0.6.0 : 48 rôles Material 3 + réussi et alerte, clair et sombre, tous à 7:1 (variante CONTENT, ADR 0004) · pour les développeurs de Marculus, Groma et Surculus · une palette entière à épingler par JitPack, au lieu d'une seule fonction de contraste (proposée par l'orchestrateur ; Pascal a ordonné fusion et tag)
- bilan du 2026-10-23 · terrain-style v0.6.0 reprenable par un successeur (documents remis à jour, marche à suivre du repli `#00843D`, prérequis des tests) · pour les développeurs de Marculus, Groma et Surculus et un futur mainteneur · une palette qu'un autre que nous sait faire évoluer, au lieu d'une palette seulement publiée (acceptée par Pascal)
- cycle 4 · plugin `imperatrice` v0.5.0 : rubrique « Valeur provisoire » et liste de contrôle avant « prêt », tirées du Pareto du bilan n° 1 · pour l'orchestrateur et les agents de tous les projets qui utilisent le plugin · une méthode qui vise 60 % des incidents du bilan, au lieu d'une palette (acceptée par Pascal)
- cycle 4 (complément) · plugin `imperatrice` v0.6.0 : rubrique « Dépend de / rouge attendu jusqu'à » · mêmes bénéficiaires que la ligne « cycle 4 » · complète cette livraison, n'en est pas une nouvelle
- bilan du 2026-10-23 (n° 2) · méthode outillée contre les causes A, B et C (plugin v0.6.0) et terrain dont l'état ouvert se lit en une minute · pour l'orchestrateur, les agents et un successeur · le successeur passe sans réserve (avec réserves au bilan n° 1) et les trois remèdes existent, pas encore en service ici (acceptée par Pascal)
- cycle 5 · mauvaise nouvelle : rien de lancé (pause décidée au bilan n° 2) ; le plugin v0.6.0 n'est pas encore installé ici (cache en 0.3.0), donc les trois remèdes du bilan n° 2 ne servent pas encore, et le vert de réussi attend toujours sa mesure · pour les développeurs de Marculus, Groma et Surculus · la prochaine livraison est l'essai au soleil de Pascal, d'ici le 2026-10-30 (acceptée par Pascal)
- cycle 6 · `terrain-style` v0.7.1, le style de Marculus (couleurs, formes, typographie, boutons), et `terrain-voix` v0.7.0 (installation sûre du modèle Vosk) · pour les développeurs de Marculus, Groma et Surculus · une seule palette, celle que Pascal a validée au soleil, au lieu d'une palette générée qui avait perdu le vert ; et un modèle de dictée qui survit aux coupures (proposée par l'orchestrateur)
