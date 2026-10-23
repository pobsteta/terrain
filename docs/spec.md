# terrain — spec du module `terrain-style`

- **Statut :** proposée (à valider par Pascal, porte n° 1)
- **Tirée de :** `docs/brief.md` (arbitrages de Pascal du 2026-10-23), issue
  [#1](https://github.com/pobsteta/terrain/issues/1)
- **Décisions structurantes :** ADR 0001 à 0003 (`docs/adr/`), proposées

## Parcours

### Sur le terrain (le forestier)

1. Il ouvre Marculus pour marteler, puis Surculus pour noter un travail, puis Groma pour
   une placette, souvent en plein soleil, téléphone tenu à bout de bras.
2. Dans les trois, il retrouve le même vert, les mêmes surfaces, et les mêmes couleurs
   pour « réussi », « alerte », « erreur ».
3. Le texte, les boutons et les états se lisent avec le contraste le plus élevé que
   Material sait produire, en thème clair par défaut.
4. À l'aube, au crépuscule ou en sous-bois, l'application peut proposer le thème sombre,
   au même contraste.

### Dans l'application (le développeur de Marculus, Groma ou Surculus)

1. Il déclare `com.github.pobsteta.terrain:terrain-style:vX.Y.Z` (Surculus : par SHA,
   ADR 0003).
2. Il recopie chaque rôle de `StyleTerrain.clair` (et `StyleTerrain.sombre`) dans son
   `ColorScheme` Compose ou son thème XML (hors du module).
3. Il prend les couleurs sémantiques dans `StyleTerrain.clair.reussi` (etc.) pour ses
   états communs.
4. Il rejoue `Contraste.rapport(a, b)` sur sa propre palette d'état dans ses tests.

## Données manipulées

| Donnée | Forme | Où |
|---|---|---|
| Couleur | ARGB 32 bits en `Long` (`0xFFRRGGBB`), alpha toujours `FF` | code |
| Schéma | un objet par thème (`clair`, `sombre`), un champ par rôle Material 3, au nom du rôle (`primary`, `onPrimary`, `surfaceContainerHighest`…) | `fr.nemeton.terrain.style` |
| Rôles couverts | tous ceux que `MaterialDynamicColors.allDynamicColors()` donne pour `SPEC_2021` au commit inscrit (liste exacte : à acquérir, voir plus bas) | code |
| Couleur sémantique | quatre rôles : `couleur`, `surCouleur`, `conteneur`, `surConteneur` ; trois couleurs : `reussi`, `alerte`, `erreur` | code |
| Réglages de génération | couleur de départ #374742, variante `FIDELITY`, `SPEC_2021`, contraste 1,0, plateforme `PHONE`, commit MCU, teintes sémantiques | en tête du code généré et `terrain-style/generation/` |
| Table de contrôle | CSV `theme,role,fond,argb_role,argb_fond,rapport` produit par le script, relu par le test (format ouvert, pour le successeur) | `terrain-style/src/test/resources/` |

API publique (noms proposés, en français comme les autres modules) :

```kotlin
object StyleTerrain { val clair: Schema; val sombre: Schema }
class Schema(val primary: Long, val onPrimary: Long, /* … tous les rôles … */,
             val reussi: Semantique, val alerte: Semantique, val erreur: Semantique)
class Semantique(val couleur: Long, val surCouleur: Long, val conteneur: Long, val surConteneur: Long)
object Contraste {
    fun luminance(argb: Long): Double   // WCAG 2.2, seuil 0,04045
    fun rapport(a: Long, b: Long): Double // (L1 + 0,05) / (L2 + 0,05), L1 ≥ L2
}
```

## Comportement

- **Génération** (une fois, hors build, ADR 0001) : le script lit les réglages, calcule les
  deux schémas et les trois couleurs sémantiques, écrit `Schema` en Kotlin et la table CSV.
  Le build ne dépend pas de Material Color Utilities.
- **Couleurs sémantiques** (ADR 0002) : teinte de référence de chaque état, harmonisée vers
  VertMarculus et déclinée en tons au contraste 1,0, comme les autres rôles.
- **Contraste** : luminance relative de WCAG 2.2 (sRGB, seuil de linéarisation 0,04045),
  rapport (L1 + 0,05) / (L2 + 0,05).
- **Test de contraste** : sur l'ARGB 8 bits final, chaque couple listé tient `rapport ≥ 7,0`
  ; le test affiche la marge de chaque couple. Une valeur qui échoue n'est pas retouchée :
  le test rend la liste des couples en échec, Pascal choisit.

## Hors périmètre

Repris du brief : `MaterialTheme` Compose ; palette d'état propre à Surculus au-delà des
trois couleurs sémantiques ; luminosité, taille et graisse du texte, polarité (aux
applications) ; niveaux de contraste 0 et 0,5 et le réglage de contraste d'Android 14+ ;
les autres briques.

## Critères d'acceptation

1. `./gradlew :terrain-style:test` passe, en CI comme en local.
2. Couples à 7:1, dans chacun des deux thèmes :
   - chaque rôle `onX` sur son fond `X` (`onPrimary`/`primary`, `onPrimaryContainer`/
     `primaryContainer`, … `onSurface`/`surface`, `onSurfaceVariant`/`surfaceVariant`,
     `inverseOnSurface`/`inverseSurface`, `onPrimaryFixed`/`primaryFixed` et
     `primaryFixedDim`, idem secondaire et tertiaire) ;
   - `onSurface` et `onSurfaceVariant` sur `surface`, `surfaceDim`, `surfaceBright` et
     chaque `surfaceContainer*` ;
   - `primary`, `secondary`, `tertiary`, `error` en texte sur `surface`,
     `surfaceContainer*` et `surfaceDim` ;
   - pour chaque couleur sémantique : `surCouleur`/`couleur`, `surConteneur`/`conteneur`,
     et `couleur` en texte sur `surface`.
3. `Contraste.rapport(0xFFFFFFFF, 0xFF3E7A40)` vaut 5,17 à 0,01 près, et les valeurs de
   référence de la table de test viennent d'un outil tiers (pas du code testé).
4. Le script, rejoué avec les réglages inscrits, redonne la même table à l'octet près.
5. Un essai au soleil (même écran, plein soleil, lecture d'une fiche en thème clair) est
   fait et noté au carnet des écarts de `pilotage/journal.md`.
6. Un tag est publié, construit par JitPack, et le README liste `terrain-style`.

## Besoins en référentiels (pour l'`acquereur`)

1. **Material Color Utilities** : commit à épingler sur `main` ; liste des rôles de
   `allDynamicColors()` en `SPEC_2021` ; courbes de contraste au niveau 1,0 ; licence
   (Apache-2.0) et conditions de reprise des sources dans un dépôt GPL-3.0-or-later.
2. **WCAG 2.2** : définitions de la luminance relative et du rapport de contraste
   (W3C, version datée), et un outil tiers de référence pour les valeurs attendues des
   tests (au moins deux, comparés).
3. **Teintes des couleurs sémantiques** : conventions publiées pour « réussi / alerte /
   erreur » (par ex. couleurs de sécurité ISO 3864-4, règles de MDC pour les couleurs
   personnalisées « harmonisées »), comparées.
4. **JitPack** : règles de construction par tag et par SHA, délai d'immutabilité.

## Questions ouvertes

- Teintes de départ de « réussi » et « alerte » (après acquisition, ADR 0002).
- Le repli prévu si une valeur échoue (« autre niveau de contraste ») n'existe plus au-delà
  de 1,0 : il ne reste que « moins de couples testés » ou une autre variante. À trancher
  par Pascal si le cas se présente.
