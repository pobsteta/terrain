# terrain — spec du module `terrain-style`

- **Statut :** validée par Pascal le 2026-10-23 (porte n° 1)
- **Tirée de :** `docs/brief.md` (arbitrages de Pascal du 2026-10-23), issue
  [#1](https://github.com/pobsteta/terrain/issues/1)
- **Décisions structurantes :** ADR 0001 à 0003 (`docs/adr/`), proposées
- **Critiquée par :** `contradicteur`, 2026-10-23 (corrections reportées)

## Parcours

### Sur le terrain (le forestier)

1. Il ouvre Marculus pour marteler, puis Surculus pour noter un travail, puis Groma pour
   une placette, souvent en plein soleil, téléphone tenu à bout de bras.
2. Dans les trois, il retrouve les mêmes surfaces, le même vert, et les mêmes couleurs
   pour « réussi », « alerte » (jaune de sécurité ISO, texte noir) et « erreur ».
3. Le texte, les boutons et les états se lisent avec le contraste le plus élevé que
   Material sait produire, en thème clair par défaut.
4. À l'aube, au crépuscule ou en sous-bois, l'application peut proposer le thème sombre,
   au même contraste.

**À savoir avant l'essai au soleil** (estimation du `contradicteur`, à confirmer par la
génération) : au niveau 1,0, VertMarculus #374742 ne se verra tel quel que sur
`primaryContainer` en clair ; `primary` sera nettement plus sombre en clair et très clair
en sombre, pour tenir 7:1.

### Dans l'application (le développeur de Marculus, Groma ou Surculus)

1. Il déclare `com.github.pobsteta.terrain:terrain-style` à un tag (Surculus : au SHA
   court du commit tagué, ADR 0003).
2. Il recopie chaque rôle de `StyleTerrain.clair` (et `StyleTerrain.sombre`) dans son
   `ColorScheme` Compose ou son thème XML (hors du module).
3. Il prend `error*` pour l'erreur, `reussi` et `alerte` pour les deux autres états
   communs, et respecte les rôles marqués « interdit en texte ».
4. Il rejoue `Contraste.rapport(a, b)` sur sa propre palette d'état dans ses tests.

## Données manipulées

| Donnée | Forme | Où |
|---|---|---|
| Couleur | ARGB 32 bits en `Long` (`0xFFRRGGBB`), alpha toujours `FF`, sans extension de signe | code |
| Schéma | un objet par thème (`clair`, `sombre`), un champ par rôle, au nom du rôle | `fr.nemeton.terrain.style` |
| Rôles couverts | **les propriétés de `ColorScheme` de Compose Material 3** (Fixed compris) : `primary` … `onTertiaryFixedVariant`, `background`, `surface*`, `inverse*`, `outline*`, `scrim`, `surfaceTint`, `error*`. Sont exclus : couleurs clés (`*PaletteKeyColor`), `*Dim` de SPEC_2025+, rôles propres à Android (`controlActivated`…) | code |
| Couleur sémantique | `reussi` et `alerte`, chacune en quatre rôles : `couleur`, `surCouleur`, `conteneur`, `surConteneur` ; l'erreur est `error*` du schéma, sans doublon | code |
| Rôle interdit en texte | liste par thème (dont `alerte.couleur`), dans le code (KDoc) et dans le README | code, `README.md` |
| Réglages de génération | couleur de départ #374742, variante `FIDELITY`, `SPEC_2021` (imposée par `FIDELITY`), contraste 1,0, plateforme `PHONE`, commit MCU, teinte de `reussi`, valeurs d'`alerte` et leur source | en tête du code généré et `terrain-style/generation/` |
| Table des jetons | CSV `theme,role,argb` produit par le script (format ouvert, pour le successeur) | `terrain-style/src/test/resources/` |
| Table de référence des rapports | CSV `argb_a,argb_b,rapport,outil,version` calculé par **deux outils tiers** nommés, pas par le code testé | `terrain-style/src/test/resources/` |

API publique (noms proposés, en français comme les autres modules) :

```kotlin
object StyleTerrain { val clair: Schema; val sombre: Schema }
class Schema(val primary: Long, val onPrimary: Long, /* … tous les rôles de ColorScheme … */,
             val reussi: Semantique, val alerte: Semantique,
             val interditsEnTexte: Set<String>)
class Semantique(val couleur: Long, val surCouleur: Long, val conteneur: Long, val surConteneur: Long)
object Contraste {
    fun luminance(argb: Long): Double   // WCAG 2.2, seuil 0,04045
    fun rapport(a: Long, b: Long): Double // (L1 + 0,05) / (L2 + 0,05), L1 ≥ L2
}
```

## Comportement

- **Génération** (une fois, hors du build publié, ADR 0001) : le script construit les deux
  schémas MCU, vérifie `scheme.specVersion == SPEC_2021`, écrit `Schema` en Kotlin et la
  table des jetons. Le build publié ne dépend pas de MCU. La CI rejoue la génération et
  échoue si le résultat diffère (`git diff --exit-code`).
- **Réussi** (ADR 0002) : un schéma MCU de plus, construit avec la palette de `reussi`
  passée à la place de celle d'`error` (`errorPalette` de `DynamicScheme`), dont on lit
  `error`, `onError`, `errorContainer`, `onErrorContainer` : mêmes courbes de contraste
  que `error`. Mécanisme à vérifier en premier ; s'il n'est pas accessible, retour à Pascal.
- **Alerte** (ADR 0002) : jaune de sécurité d'ISO 3864-4, **`#E2AA00`** (Table 1, β 0,45,
  D65, converti en sRGB ; choix de Pascal du 2026-10-23, `docs/referentiels/couleurs-etat.md`)
  en `couleur` et `conteneur`, noir `#000000` en `surCouleur` et `surConteneur` (9,98:1),
  dans les deux thèmes ; valeurs figées, source inscrite ; `alerte.couleur` est interdit
  en texte.
- **Erreur** : rôles `error*` du schéma, palette par défaut de MCU, non harmonisée.
- **Contraste** : luminance relative de WCAG 2.2 (sRGB, seuil de linéarisation 0,04045),
  rapport (L1 + 0,05) / (L2 + 0,05).
- **Test de contraste** : sur l'ARGB 8 bits final, chaque couple listé tient
  `rapport ≥ 7,0`, rapport **tronqué** à 3 décimales, jamais arrondi (WCAG : 4,499:1 ne
  tient pas 4,5:1) ; le test affiche la marge de chaque couple. Une valeur qui échoue n'est
  pas retouchée : le couple est listé et le rôle passe « interdit en texte ».

## Hors périmètre

Repris du brief : `MaterialTheme` Compose ; palette d'état propre à Surculus au-delà de
réussi, alerte et erreur ; luminosité, taille et graisse du texte, polarité (aux
applications) ; l'icône ou le libellé qui double chaque couleur d'état (WCAG 1.4.1, aux
applications) ; niveaux de contraste 0 et 0,5 et le réglage de contraste d'Android 14+ ;
les autres briques.

## Critères d'acceptation

1. `./gradlew :terrain-style:test` passe, en CI comme en local.
2. Couples à 7:1, dans chacun des deux thèmes, sauf rôle « interdit en texte » :
   - chaque `onX` sur son fond `X` : `primary`, `primaryContainer`, `secondary`,
     `secondaryContainer`, `tertiary`, `tertiaryContainer`, `error`, `errorContainer`,
     `background`, `surface`, `surfaceVariant`, `inverseOnSurface`/`inverseSurface` ;
   - `onXFixed` et `onXFixedVariant` sur `XFixed` et `XFixedDim` (primaire, secondaire,
     tertiaire) ;
   - `onSurface` et `onSurfaceVariant` sur `surface`, `surfaceDim`, `surfaceBright` et
     chaque `surfaceContainer*` ;
   - `primary`, `secondary`, `tertiary`, `error` et `reussi.couleur` en texte sur
     `surface`, `surfaceDim`, `surfaceBright` et chaque `surfaceContainer*` ;
   - `inversePrimary` sur `inverseSurface` ;
   - `surCouleur`/`couleur` et `surConteneur`/`conteneur` de `reussi` et d'`alerte`.
3. `Contraste.rapport` redonne la table de référence des deux outils tiers, dont
   `0xFFFFFFFF` / `0xFF3E7A40` = 5,17 à 0,01 près.
4. Le script, rejoué avec les réglages inscrits, redonne la table des jetons à l'octet
   près (ARGB seulement), en CI.
5. Chaque jeton est dans [`0xFF000000`, `0xFFFFFFFF`].
6. Un essai au soleil (même écran, plein soleil, lecture d'une fiche en thème clair) est
   fait et noté au carnet des écarts de `pilotage/journal.md`.
7. Un tag est publié sur un commit de `main` à CI verte, construit par JitPack, et le
   README liste `terrain-style` et les rôles interdits en texte.

## Besoins en référentiels (pour l'`acquereur`)

1. **Material Color Utilities** : commit à épingler ; parité des sources Java et Kotlin
   (générer les deux, comparer) et version de Kotlin exigée ; visibilité du constructeur de
   `DynamicScheme` qui prend `errorPalette` ; licence Apache-2.0 et obligations du §4.
2. **Compose Material 3** : liste des propriétés de `ColorScheme`, à une version datée.
3. **WCAG 2.2** : définitions de la luminance relative et du rapport de contraste
   (W3C, version datée), et deux outils tiers pour la table de référence, comparés.
4. **Couleurs d'état** : teinte de référence de « réussi » (ISO 3864-1, ISO 22324,
   IEC 60073, MDC « custom colors », comparés) ; jaune de sécurité et sa couleur de
   contraste (ISO 3864-1, ISO 3864-4 pour la colorimétrie) avec une valeur sRGB sourcée ;
   teintes HCT avant et après harmonisation, et écart de `reussi` à `primary`.
5. **JitPack** : construction par tag et par SHA court, délai d'immutabilité.

## Questions ouvertes

- Teinte de départ de « réussi », et faut-il l'harmoniser vers VertMarculus au risque de la
  rapprocher de `primary` (après mesure de l'`acquereur`, ADR 0002).
- Valeur exacte du jaune d'alerte (après acquisition, ADR 0002).
