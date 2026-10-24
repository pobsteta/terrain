# terrain — brief

> Statut : validé par Pascal le 2026-10-23 (porte n° 1) ; critiqué par le `contradicteur`
> avec `/imperatrice:brief`.
>
> Premier objet : le module `terrain-style`, demandé par
> [Surculus](https://github.com/pobsteta/surculus) dans l'issue
> [#1](https://github.com/pobsteta/terrain/issues/1) (2026-10-23), qui reprend
> `surculus/pilotage/briefs/vers-terrain/2026-10-23-terrain-style.md` (commit `573972a`)
> et va avec l'ADR 0008 de Surculus. Les rubriques ci-dessous en reprennent le contenu,
> amendé par les arbitrages de Pascal du 2026-10-23 (en fin de document) ; les chemins
> `docs/adr/…` cités sont ceux de Surculus.

## Ma réponse d'abord (fiche n° 15)

> J'attends une cohérence des applications pour terrain-style et la capacité profonde d'un
> grand contraste pour une utilisation sur le terrain en extérieur.
>
> — Pascal, le 23 octobre 2026, écrit avant de lire la critique du contradicteur.

## Le problème, et pour qui (Abstraction)

- **Problème** : un forestier passe de Marculus à Groma ou à Surculus, en plein soleil, et
  les trois applications n'ont ni les mêmes couleurs ni le même contraste. Marculus et
  Groma ne fixent que cinq rôles Material 3 ; le reste est le défaut de Material 3. Le
  texte blanc sur `VertVif` #3E7A40 ne donne que 5,17:1, sous le plancher de 7:1 retenu
  (WCAG 2.2, 1.4.6). Le style viendrait sinon d'une copie de `Theme.kt` d'une appli à
  l'autre.
- **Bénéficiaires** : d'abord les trois applications (Surculus en premier, qui épinglera
  le module), ensuite les forestiers qui les lisent sur le terrain, en plein soleil.
- **Coût complet aujourd'hui** : inconnu.

## Ce que fait terrain (module `terrain-style`)

- **En une phrase** : fournit les jetons de couleur Material 3 communs aux applications de
  terrain Néméton, au contraste le plus élevé de Material, figés dans le code et garantis
  à 7:1 par un test.
- **Les gestes sur le terrain** : aucun directement ; l'utilisateur lit un écran au soleil.
- **Ce qu'il produit** :
  1. **Tous les rôles** du schéma de couleurs Material 3, en ARGB (`Long`), en **thème
     clair (défaut sur le terrain)** et en **thème sombre** (fourni pour l'aube, le
     crépuscule et le sous-bois).
  2. **Des valeurs générées une fois** par Material Color Utilities (Google, Apache-2.0)
     depuis la couleur de départ `VertMarculus` #374742, **variante `CONTENT`** (ADR 0004, qui amende
     `FIDELITY`, d'abord retenue ; la graine reste en `primaryContainer` en clair), au **niveau de contraste 1,0 (élevé), un seul niveau**,
     schéma **`SPEC_2021`**, puis **figées** dans le code, sans dépendance à l'exécution.
     Sont inscrits dans le module : le script de génération, le commit de Material Color
     Utilities, la version du schéma, la variante, le niveau de contraste et la couleur de
     départ.
  3. **Des couleurs sémantiques communes** aux trois applications : réussi, alerte, erreur,
     en clair et en sombre, soumises au même test. **Erreur** est le rôle `error` de
     Material, non doublé ; **réussi** a ses quatre rôles (couleur, texte dessus,
     conteneur, texte sur conteneur) ; **alerte** suit la convention de sécurité : jaune
     de sécurité `#E2AA00` en fond (ISO 3864-4, arbitrage du 2026-10-23 sur les référentiels), noir dessus (couleur de contraste d'ISO 3864-1), et son jaune n'est
     jamais employé en texte.
  4. **Un test de contraste** (WCAG 2.2, luminance relative) sur **chaque couple** rôle
     « on » / rôle de fond, et sur `primary`, `secondary`, `tertiary`, `error` employés en
     texte sur `surface`, `surfaceContainer*` et `surfaceDim`. Seuil : **7:1** (WCAG 2.2,
     1.4.6, décision de Pascal ; l'appliquer à tous les jetons, grand texte compris, est
     une déduction de l'orchestrateur de Surculus : le jeton ne sait pas à quelle taille il
     servira). Le test est juge : une valeur générée qui échoue ne passe pas.
     **7:1 est un plancher, pas une garantie de lecture au soleil** : WCAG mesure des
     couleurs émises sans lumière ambiante, et au soleil le reflet de l'écran écrase tous
     les rapports.
  5. **Une fonction publique de contraste** (rapport entre deux ARGB), pour que chaque
     application rejoue le test sur sa propre palette d'état.
  6. **Un tag publié**, construit par JitPack. Surculus l'épinglera par SHA de commit, au
     moins 7 jours après sa publication (ADR 0008 de Surculus, lettre A).
- **Critère d'acceptation** : `./gradlew :terrain-style:test` passe ; tous les couples du
  point 4 tiennent 7:1 ; le script de génération, rejoué avec les réglages inscrits,
  redonne les mêmes valeurs ; **un essai au soleil** (même écran, plein soleil, lecture
  d'une fiche en clair) est fait et noté au carnet des écarts de `pilotage/journal.md`.

## Ce qu'il ne fait pas (hors périmètre)

- Le `MaterialTheme` Compose : chaque application recopie les rôles dans son
  `ColorScheme` (option A écartée).
- La palette d'état propre à Surculus (pastilles, badge, alertes) au-delà des trois
  couleurs sémantiques communes : elle reste dans Surculus.
- Ce qui fait la lisibilité au soleil hors des couleurs, et reste aux applications :
  luminosité de l'écran, taille et graisse minimales du texte, polarité claire par défaut.
- Les niveaux de contraste standard (0) et moyen (0,5), et le réglage de contraste
  d'Android 14 et suivants.
- Les autres briques (GNSS, journal, GeoPackage…) : ADR 0012 de Surculus, réservée.

## Contraintes connues

- Kotlin pur, JVM 17, sans Android ni Compose (règle de `README.md`, décision du
  2026-10-17) ; compilé avec Kotlin 2.2.
- JitPack construit sans les tests (`jitpack.yml`, `-x test`) ; la CI
  (`.github/workflows/ci.yml`) les joue à chaque push et chaque PR. Surculus rejoue quand
  même le test de contraste sur les jetons reçus.
- **Si une valeur générée échoue au test** : ne pas la retoucher à la main. Le couple en
  échec est listé, et le rôle concerné est documenté « interdit en texte » ; le seuil
  reste 7:1 (décision de Pascal du 2026-10-23, qui remplace le repli de l'issue #1 :
  « autre niveau de contraste » n'existe plus au-delà de 1,0, « moins de couples testés »
  est écarté).
- **Plafond de 7,0** : dans `SPEC_2021`, `primary`, `secondary`, `tertiary` et `error`
  visent exactement 7,0 même au niveau 1,0, mesuré contre la surface claire la plus
  sombre (`surfaceDim`) ; Material Color Utilities tolère un écart de 0,04. Après
  arrondi 8 bits, ces couples peuvent tomber juste sous 7:1. (Le point « contre
  `surfaceDim` ou `surfaceContainerHighest` » de l'issue #1 est levé par construction,
  d'après la critique du contradicteur.)
- **Successeur de `terrain`** : aucun pour l'instant, risque assumé par Pascal (ADR 0008
  de Surculus, question 3, 2026-10-23).

## Arbitrages de Pascal (2026-10-23, après la critique du contradicteur)

> Ces arbitrages se lisent dans l'ordre : **une section postérieure remplace la section
> antérieure** sur le même point (la variante `FIDELITY`, puis `CONTENT` ; le « jaune clair »,
> puis `#E2AA00` ; le vert de `reussi`). Les sections plus haut dans ce document ont été mises
> à jour ; ces sections-ci sont le registre daté et ne sont pas réécrites.

- **Niveau de contraste** : un seul, 1,0 (élevé) ; ni 0 ni 0,5.
- **Variante** : `FIDELITY`, VertMarculus doit se voir.
- **Thème sombre** : clair par défaut sur le terrain, sombre fourni.
- **Retenus** : 7:1 décrit comme plancher et essai au soleil ; couleurs sémantiques
  communes (réussi, alerte, erreur).
- **Non retenus** : table explicite des couples avec 3:1 pour le non-texte et retrait du
  repli « moins de couples testés » ; garanties de cohérence (table rôle → ARGB avec test
  « tous les rôles mappés » dans chaque application, `dynamicColor` désactivé, adoption
  par les trois applications en critère d'acceptation).

## Arbitrages de Pascal (2026-10-23, après la critique de la spec et des ADR)

- **Erreur** : le rôle `error` de Material, non harmonisé, exposé une seule fois.
- **Alerte** : jaune clair en fond et noir dessus (convention de sécurité), hors de Material
  Color Utilities pour ce jaune ; jaune interdit en texte.
- **Repli** : un rôle qui échoue à 7:1 est documenté « interdit en texte ».
- **Corrections techniques** de la spec et des ADR 0001 et 0003 : toutes appliquées.

## Arbitrages de Pascal (2026-10-23, sur les référentiels du cycle 1)

- **Alerte** : jaune de sécurité d'ISO 3864-4, `#E2AA00` (ambre), texte noir ; remplace
  « jaune clair » des arbitrages précédents.
- **Réussi** : le vert de départ attend la mesure HCT de la mission 007.
- **Normes** : ISO 3864-1 et l'annexe E d'ISO 3864-4 ne sont pas achetées pour l'instant.
- **Référentiel MCU** : Kotlin 1.8 et JDK 14, cités de mémoire, restent marqués « non
  sourcés » ; la mission 006 les confirmera.

## Arbitrages de Pascal (2026-10-23, sur les mesures de la mission 007)

- **Tertiary** : variante `CONTENT` de MCU au lieu de `FIDELITY` (ADR 0004), parce qu'en
  thème sombre `tertiary` était identique à `error` (`#FFECE9`).
- **Réussi** : `#0F8558`, non harmonisé ; repli `#00843D` si l'essai au soleil le confond
  avec `primary`.
- **Bord de la pastille d'alerte** sur surface claire (1,06 à 2,10:1) : noté au carnet des
  écarts, à vérifier pendant l'essai au soleil.


---

# Deuxième objet : corrections de `terrain-voix` (issue #23)

> Statut : proposé, porte n° 1 à passer. Tiré du brief de Surculus du 2026-10-24
> (`surculus/pilotage/briefs/vers-terrain/2026-10-24-ecarts-terrain-voix.md`, issue
> [#23](https://github.com/pobsteta/terrain/issues/23)), critiqué par le `contradicteur` le
> 2026-10-24, avec les arbitrages de Pascal du même jour. Décision : ADR 0005.

## Ma réponse d'abord (fiche n° 15)

_À écrire par Pascal. Le `contradicteur` a été lancé avant, sur l'ordre de Pascal du
2026-10-24 (journal)._

## Le problème, et pour qui (Abstraction)

- **Problème** : un import ou une mise à jour ratés du modèle Vosk (archive refusée ou
  illisible, coupure réseau, annulation) effacent le modèle qui marchait. Une archive vide ou
  mal formée peut aussi être déclarée « installée ». Le forestier découvre en forêt, hors
  réseau, que la dictée ne marche plus.
- **Bénéficiaires** : d'abord les développeurs de Surculus et de Marculus, qui utilisent
  `InstallationModele`, puis les forestiers qui dictent sur le terrain. Groma n'utilise pas ces
  classes.
- **Coût complet aujourd'hui** : inconnu.

## Ce que fait la correction

- **En une phrase** : un échec d'installation laisse intact le modèle en place, un modèle
  n'est déclaré installé que s'il a la structure d'un modèle Vosk à grammaire dynamique, et l'appelant dit
  explicitement s'il contrôle l'empreinte.
- **Critère d'acceptation** : chaque constat du brief de Surculus a un test dans
  `terrain-voix` qui le montre corrigé (spec, partie `terrain-voix`) ; v0.7.0 publiée ;
  Surculus remplace son test « ECART terrain-voix » par le cas de l'archive à bonne empreinte
  mais illisible, et ses 162 cas de `DicteeRejeuTest` restent verts.

## Ce qu'elle ne fait pas (hors périmètre)

- Le catalogue des modèles dans terrain (URL et empreinte communes) : option D de l'ADR 0005,
  écartée pour l'instant.
- La montée de version dans Marculus et Surculus : elle se fait dans leurs dépôts.
- Changer le type de `motsConnus`.

## Arbitrages de Pascal (2026-10-24, après la critique du contradicteur)

- **Échec** : l'ancien modèle est gardé (décompression à côté, deux renommages, reprise
  après crash) ; le test de terrain et la KDoc « Échec → dossier effacé » sont inversés.
- **Disque plein** : la mise à jour est refusée, et le modèle en place est gardé.
- **Empreinte** : choix explicite (`Empreinte.Sha256` ou `Empreinte.NonControlee`), sans
  valeur par défaut ; v0.7.0.
- **Vocabulaire** : `motsConnus` inchangé ; une fonction ajoutée distingue « illisible » de
  « connu ».

## Arbitrages de Pascal (2026-10-24, après la critique de l'ADR 0005 et de la spec)

- **Structure** : contrôle des fichiers d'un modèle Vosk et détection du dossier racine ;
  jeux d'essai refaits.
- **Marge d'espace libre** : provisoire, proposée par le `dev`, fixée par Pascal.
- **Modèles à `HCLG.fst`** (sans grammaire dynamique) : refusés ; pas de repli sur
  `words.txt` pour le vocabulaire.
- **Reprise** : quand le nouveau modèle est vérifié, on avance (bascule terminée) ; sinon,
  on remet l'ancien.
- **Corrections techniques** de la seconde critique (table d'états, `estInstalle` sans
  écriture, verrou, espace injectable, erreurs typées, jeux d'essai figés, écriture forcée
  sur le disque, source de la version) : toutes appliquées.
