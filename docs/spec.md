# terrain — spec du module `terrain-style`

- **Statut :** validée par Pascal le 2026-10-23 (porte n° 1)
- **Tirée de :** `docs/brief.md` (arbitrages de Pascal du 2026-10-23), issue
  [#1](https://github.com/pobsteta/terrain/issues/1)
- **Décisions structurantes :** ADR 0001 à 0004 (`docs/adr/`), acceptées par Pascal le 2026-10-23
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

**À savoir avant l'essai au soleil** (constat de la génération, mission 006 ; valeurs dans
`terrain-style/src/test/resources/jetons/jetons.csv`, nuancier dans
`terrain-style/generation/nuancier.svg`) : au niveau 1,0, VertMarculus #374742 ne se voit tel
quel que sur `primaryContainer` en clair ; `primary` est nettement plus sombre en clair
(#202F2B) et très clair en sombre (#E2F4ED), pour tenir 7:1. L'estimation du `contradicteur`
est confirmée.

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
| Couleur | ARGB 32 bits en `Long` (`0xFFRRGGBB`), alpha toujours `FF`, sans extension de signe ; toute autre valeur est refusée par `IllegalArgumentException` (Pascal, 2026-10-23) | code |
| Schéma | un objet par thème (`clair`, `sombre`), un champ par rôle, au nom du rôle | `fr.nemeton.terrain.style` |
| Rôles couverts | **les propriétés de `ColorScheme` de Compose Material 3** (Fixed compris) : `primary` … `onTertiaryFixedVariant`, `background`, `surface*`, `inverse*`, `outline*`, `scrim`, `surfaceTint`, `error*`. Sont exclus : couleurs clés (`*PaletteKeyColor`), `*Dim` de SPEC_2025+, rôles propres à Android (`controlActivated`…) | code |
| Couleur sémantique | `reussi` et `alerte`, chacune en quatre rôles : `couleur`, `surCouleur`, `conteneur`, `surConteneur` ; l'erreur est `error*` du schéma, sans doublon | code |
| Rôle interdit en texte | liste par thème (dont `alerte.couleur`), dans le code (KDoc) et dans le README | code, `README.md` |
| Réglages de génération | couleur de départ #374742, variante `CONTENT` (ADR 0004, au lieu de `FIDELITY`), `SPEC_2021` (imposée par la variante), contraste 1,0, plateforme `PHONE`, commit MCU, teinte de `reussi`, valeurs d'`alerte` et leur source | en tête du code généré et `terrain-style/generation/` |
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
  que `error`. Mécanisme **vérifié** (mission 001) : le constructeur `DynamicScheme(errorPalette)` est public ;
  `reussi` est généré ainsi (mission 007).
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
   fait et noté au carnet des écarts de `pilotage/journal.md`. *Fait le 2026-10-24 par Pascal,
   dans Marculus : style validé en clair et en sombre.*
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

Aucune. L'essai au soleil (critère 6) est fait le 2026-10-24 par Pascal, dans Marculus : style
validé en clair et en sombre. Les deux questions d'origine sont tranchées par Pascal le
2026-10-23 (ADR 0002, notes « levé le » et « Amendé le ») : teinte de départ de « réussi »
`#0F8558`, non harmonisé, **gelée le 2026-10-24** (ADR 0002, note « Gel ») ; jaune d'alerte
`#E2AA00`.

---

# Spec de `terrain-voix` : installation du modèle (issue #23)

- **Statut :** proposée, porte n° 1 à passer
- **Tirée de :** `docs/brief.md`, partie « Deuxième objet » ; décision : ADR 0005 (proposée)

## Parcours

1. **Sur le terrain** : le forestier a un modèle qui marche. Il lance une mise à jour (Wi-Fi)
   ou un import par fichier. Si l'opération échoue ou s'il l'annule, il garde son modèle et
   un message lui dit pourquoi.
2. **Dans l'application** : le développeur construit `InstallationModele(dossier, cache, url,
   empreinte)` en passant `Empreinte.Sha256(hex)` ou `Empreinte.NonControlee`. Il appelle
   `telecharger` ou `installer`, et lit `estInstalle`.

## Comportement

- **Bascule.** On décompresse dans `<dossier>.nouveau` (même parent que `dossier`), on
  contrôle la structure, on pose le témoin dans `.nouveau`, puis on renomme `dossier` en
  `<dossier>.ancien`, `.nouveau` en `dossier`, et on efface `.ancien`. Tout échec avant le
  second renommage laisse `dossier` intact et efface `.nouveau`.
- **Reprise.** Au début de chaque opération et dans `estInstalle` : si `dossier` n'a pas de
  témoin et que `<dossier>.ancien` en a un, `.ancien` est remis en place ; les restes
  (`.nouveau`, `.ancien` après bascule réussie) sont effacés.
- **Espace.** Avant de télécharger ou de décompresser, `File.usableSpace` du parent de
  `dossier` doit couvrir la taille décompressée annoncée par l'archive, avec une marge. Sinon,
  refus avec l'espace manquant dans le message ; rien n'est effacé. La marge est une valeur
  provisoire, à fixer par le `dev` et à soumettre à Pascal.
- **Structure.** Un modèle est valide s'il contient `am/final.mdl`, `conf/model.conf`, et
  `graph/HCLG.fst` ou bien `graph/HCLr.fst` avec `graph/Gr.fst` (alphacephei, « Vosk
  Models »). Le dossier racine de l'archive est détecté (un seul dossier de tête commun), et
  non supposé.
- **Empreinte.** `Empreinte.Sha256(hex)` refuse à la construction tout ce qui n'est pas 64
  caractères hexadécimaux, avec un message qui le dit. Au contrôle, un écart donne un message
  avec l'empreinte attendue et l'empreinte obtenue.
- **Vocabulaire.** `VocabulaireVosk.motsConnus` est inchangé. La fonction ajoutée rend
  `Connus(mots)` ou `Illisible(raison)`, et lit `graph/words.txt` quand `Gr.fst` manque.

## Critères d'acceptation

Un test par cas dans `terrain-voix`. Chacun vérifie que **le contenu** de l'ancien modèle est
intact (un fichier présent, un fichier du nouveau absent), pas seulement le témoin :

1. empreinte fausse ;
2. archive à bonne empreinte mais tronquée ;
3. archive vide (0 octet), zip sans entrée, zip avec seulement le dossier racine ;
4. archive sans structure Vosk, et archive sans dossier racine (bien installée si la
   structure y est) ;
5. annulation et coupure de `telecharger` sur un modèle installé (le test actuel est
   inversé) ;
6. crash simulé entre les deux renommages, puis reprise ;
7. espace insuffisant ;
8. forme d'empreinte invalide (vide, 63 caractères, caractère non hexadécimal) ;
9. vocabulaire : `Gr.fst` illisible donne `Illisible` ; `words.txt` seul donne `Connus`.

Les empreintes de référence des tests sont calculées par `sha256sum` (GNU coreutils), pas par
le code testé. `./gradlew test` passe en CI. Le README décrit le nouveau constructeur et la
note de version de v0.7.0 prévient Marculus.

## Questions ouvertes

- La marge d'espace libre (valeur provisoire, voir « Espace »).
- Les corrections de structure (contrôle des fichiers Vosk, racine détectée) : proposées par
  le `contradicteur`, à valider par Pascal à la porte n° 1.
