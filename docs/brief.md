# terrain — brief

> À remplir par Pascal ; critiqué par le `contradicteur` avec `/imperatrice:brief`.
>
> Premier objet : le module `terrain-style`, demandé par Surculus dans l'issue
> [#1](https://github.com/pobsteta/terrain/issues/1) (2026-10-23), qui reprend
> `surculus/pilotage/briefs/vers-terrain/2026-10-23-terrain-style.md` (commit `573972a`)
> et va avec l'ADR 0008 de Surculus. Les rubriques ci-dessous en reprennent le contenu ;
> les chemins `docs/adr/…` cités sont ceux de Surculus.

## Ma réponse d'abord (fiche n° 15)

> J'attends une cohérence des applications pour terrain-style et la capacité profonde d'un
> grand contraste pour une utilisation sur le terrain en extérieur.
>
> — Pascal, le 23 octobre 2026, écrit avant de lire la critique du contradicteur.

## Le problème, et pour qui (Abstraction)

- **Problème** : Marculus, Groma et Surculus n'ont pas de style commun. Marculus et Groma
  ne fixent que cinq rôles Material 3 ; le reste est le défaut de Material 3. Le texte
  blanc sur `VertVif` #3E7A40 ne donne que 5,17:1, sous le seuil retenu pour la lecture
  au soleil. Le style viendrait sinon d'une copie de `Theme.kt` d'une appli à l'autre.
- **Bénéficiaires** : d'abord les trois applications (Surculus en premier, qui épinglera
  le module), ensuite les forestiers qui les lisent sur le terrain, en plein soleil.
- **Coût complet aujourd'hui** : _…_

## Ce que fait terrain (module `terrain-style`)

- **En une phrase** : fournit les jetons de couleur Material 3 communs aux applications de
  terrain Néméton, figés dans le code et garantis à 7:1 de contraste par un test.
- **Les gestes sur le terrain** : aucun directement ; l'utilisateur lit un écran au soleil.
- **Ce qu'il produit** :
  1. **Tous les rôles** du schéma de couleurs Material 3, en ARGB (`Long`), en thème clair ;
     en sombre aussi si Marculus ou Groma garde son mode sombre.
  2. **Des valeurs générées une fois** par Material Color Utilities (Google, Apache-2.0)
     depuis la couleur de départ `VertMarculus` #374742, au **niveau de contraste 0,5**,
     schéma **`SPEC_2021`**, puis **figées** dans le code, sans dépendance à l'exécution.
     Sont inscrits dans le module : le script de génération, le commit de Material Color
     Utilities, la version du schéma, le niveau de contraste et la couleur de départ.
  3. **Un test de contraste** (WCAG 2.2, luminance relative) sur **chaque couple** rôle
     « on » / rôle de fond, et sur `primary`, `secondary`, `tertiary`, `error` employés en
     texte sur `surface`, `surfaceContainer*` et `surfaceDim`. Seuil : **7:1** (WCAG 2.2,
     1.4.6, décision de Pascal ; l'appliquer à tous les jetons, grand texte compris, est
     une déduction de l'orchestrateur de Surculus : le jeton ne sait pas à quelle taille il
     servira). Le test est juge : une valeur générée qui échoue ne passe pas.
  4. **Une fonction publique de contraste** (rapport entre deux ARGB), pour que chaque
     application rejoue le test sur sa propre palette d'état.
  5. **Un tag publié**, construit par JitPack. Surculus l'épinglera par SHA de commit, au
     moins 7 jours après sa publication (ADR 0008 de Surculus, lettre A).
- **Critère d'acceptation** : `./gradlew :terrain-style:test` passe ; tous les couples du
  point 3 tiennent 7:1 ; le script de génération, rejoué avec les réglages inscrits,
  redonne les mêmes valeurs.

## Ce qu'il ne fait pas (hors périmètre)

- Le `MaterialTheme` Compose : chaque application recopie les rôles dans son
  `ColorScheme` (option A écartée).
- La palette d'état de Surculus (pastilles, badge, alertes) : elle reste dans Surculus.
- Les autres briques (GNSS, journal, GeoPackage…) : ADR 0012 de Surculus, réservée.

## Contraintes connues

- Kotlin pur, JVM 17, sans Android ni Compose (règle de `README.md`, décision du
  2026-10-17) ; compilé avec Kotlin 2.2.
- JitPack construit sans les tests (`jitpack.yml`, `-x test`) ; la CI
  (`.github/workflows/ci.yml`) les joue à chaque push et chaque PR. Surculus rejoue quand
  même le test de contraste sur les jetons reçus.
- **Si une valeur générée échoue au test** : ne pas la retoucher à la main. Rendre la
  liste des couples en échec ; Pascal choisit l'issue (décision du 2026-10-23) : autre
  niveau de contraste de Material Color Utilities, ou moins de couples testés.
- **À vérifier** : dans `SPEC_2021`, la cible de 7:1 de `primary`, `secondary` et
  `tertiary` se mesure contre une seule surface ; contre `surfaceDim` ou
  `surfaceContainerHighest`, ils peuvent tomber sous 7:1.
- **Successeur de `terrain`** : aucun pour l'instant, risque assumé par Pascal (ADR 0008
  de Surculus, question 3, 2026-10-23).
