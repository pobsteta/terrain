# terrain — spec du module `terrain-style`

- **Statut :** validée par Pascal le 2026-10-23 (porte n° 1)
- **Tirée de :** `docs/brief.md` (arbitrages de Pascal du 2026-10-23), issue
  [#1](https://github.com/pobsteta/terrain/issues/1)
- **Décisions structurantes :** ADR 0006 (2026-10-24, **le style de Marculus**), qui remplace
  ADR 0001 et 0004 ; ADR 0002 (réussi, alerte) et 0003 (publication) restent
- **Révisée le 2026-10-24** (cap de Pascal, ADR 0006) : `terrain-style` reprend le style de
  Marculus, couleurs, formes, typographie et boutons ; la règle 7:1 tombe comme règle commune
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
| Source du style | thème de Marculus à un tag nommé (aujourd'hui v0.65.0, `Theme.kt` l. 10-28) et valeurs par défaut de Compose Material 3 au BOM de Marculus (`2026.02.01`) pour tout ce que Marculus ne fixe pas (ADR 0006) | `docs/referentiels/style-marculus.md` |
| Table des jetons | CSV `theme,role,argb` recopié du référentiel (format ouvert, pour le successeur), source des tests d'égalité | `terrain-style/src/test/resources/` |
| Formes | rayons d'angle de `Shapes` (`extraSmall` … `extraLarge`), en dp, plus les formes propres à Marculus (par exemple la pilule `RoundedCornerShape(50)`) | code |
| Typographie | les 15 styles de `Typography` (`displayLarge` … `labelSmall`) : taille et interligne en sp, graisse, interlettrage | code |
| Boutons | forme (pilule : `RoundedCornerShape(50)`), hauteurs minimales et marges intérieures des boutons de Material 3 1.4.0, en dp, celles que Marculus emploie | code |
| Table de référence des rapports | CSV `argb_a,argb_b,rapport,outil,version` calculé par **deux outils tiers** nommés, pas par le code testé | `terrain-style/src/test/resources/` |

API publique (noms proposés, en français comme les autres modules) :

```kotlin
object StyleTerrain { val clair: Schema; val sombre: Schema }
class Schema(val primary: Long, val onPrimary: Long, /* … tous les rôles de ColorScheme … */,
             val reussi: Semantique, val alerte: Semantique,
             val interditsEnTexte: Set<String>)
class Semantique(val couleur: Long, val surCouleur: Long, val conteneur: Long, val surConteneur: Long)
object FormesTerrain { /* rayons en dp, nommés comme Shapes */ }
object TypoTerrain { /* un Style(tailleSp, interligneSp, graisse, interlettrageSp) par rôle de Typography */ }
object BoutonsTerrain { /* hauteurs minimales, rayon, marges, en dp */ }
object Contraste {
    fun luminance(argb: Long): Double   // WCAG 2.2, seuil 0,04045
    fun rapport(a: Long, b: Long): Double // (L1 + 0,05) / (L2 + 0,05), L1 ≥ L2
}
```

## Comportement

- **Valeurs** (ADR 0006) : les 48 rôles, les formes, la typographie et les boutons sont
  recopiés du référentiel `docs/referentiels/style-marculus.md`, lui-même relevé dans
  Marculus et dans Compose Material 3. Ils ne sont plus générés par Material Color Utilities.
  Pour changer le style, on change d'abord Marculus, puis le référentiel, puis le code, par
  une note datée de l'ADR 0006.
- **Réussi** (ADR 0002) : valeurs de v0.6.1 gardées, gelées le 2026-10-24. Elles avaient été obtenues par un schéma MCU de plus, construit avec la palette de `reussi`
  passée à la place de celle d'`error` (`errorPalette` de `DynamicScheme`), dont on lit
  `error`, `onError`, `errorContainer`, `onErrorContainer` : mêmes courbes de contraste
  que `error`. Mécanisme **vérifié** (mission 001) : le constructeur `DynamicScheme(errorPalette)` est public ;
  `reussi` est généré ainsi (mission 007).
- **Alerte** (ADR 0002) : jaune de sécurité d'ISO 3864-4, **`#E2AA00`** (Table 1, β 0,45,
  D65, converti en sRGB ; choix de Pascal du 2026-10-23, `docs/referentiels/couleurs-etat.md`)
  en `couleur` et `conteneur`, noir `#000000` en `surCouleur` et `surConteneur` (9,98:1),
  dans les deux thèmes ; valeurs figées, source inscrite ; `alerte.couleur` est interdit
  en texte.
- **Erreur** : rôles `error*` du schéma, valeurs par défaut de Material 3 (Marculus ne les fixe pas).
- **Contraste** : luminance relative de WCAG 2.2 (sRGB, seuil de linéarisation 0,04045),
  rapport (L1 + 0,05) / (L2 + 0,05).
- **Contraste, pour information** (ADR 0006) : la règle 7:1 tombe comme règle commune. Un
  test affiche le rapport des couples de la liste du critère 2, sans échouer. `Contraste`
  reste publique pour que chaque application mesure ses propres couples.
  `interditsEnTexte` ne contient plus que `alerte.couleur`, par décision (ADR 0002).
- **Réussi et `primary`** : l'écart de teinte HCT entre `reussi.couleur` et `primary`
  (`VertVif`) est mesuré et inscrit dans le référentiel, en clair et en sombre (brief de
  Marculus, § 2.2).

## Hors périmètre

Repris du brief : `MaterialTheme` Compose (chaque application recopie les valeurs) ;
palette d'état propre à Surculus au-delà de réussi, alerte et erreur ; luminosité, polarité
(aux applications) ; l'icône ou le libellé qui double chaque couleur d'état (WCAG 1.4.1, aux
applications) ; niveaux de contraste 0 et 0,5 et le réglage de contraste d'Android 14+ ;
les autres briques.

## Critères d'acceptation

1. `./gradlew :terrain-style:test` passe, en CI comme en local.
2. **Égalité au style de Marculus** (ADR 0006) : chacun des 48 rôles, dans chacun des deux
   thèmes, est égal à la valeur du référentiel ; de même chaque forme, chaque style de
   typographie et chaque dimension de bouton. *Ancien critère « couples à 7:1 », remplacé le
   2026-10-24 ; la liste qui suit sert au test informatif du contraste :*
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
4. Le nuancier, clair et sombre, est montré à Pascal avant la publication, et il dit « c'est
   le vert de Marculus ». *Ancien critère « le script MCU redonne la table », remplacé le
   2026-10-24.*
5. Chaque jeton est dans [`0xFF000000`, `0xFFFFFFFF`].
6. Un essai au soleil (même écran, plein soleil, lecture d'une fiche en thème clair) est
   fait et noté au carnet des écarts de `pilotage/journal.md`. *Fait le 2026-10-24 par Pascal,
   dans Marculus : style validé en clair et en sombre.*
7. Un tag est publié sur un commit de `main` à CI verte, construit par JitPack, et le
   README liste `terrain-style` et les rôles interdits en texte. Pour le style de Marculus,
   c'est **v0.6.2**.
8. Marculus réadopte `terrain-style` sans changement visible : sa capture avant et après est
   identique, en clair et en sombre (sa mission 006).

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

- **Statut :** validée par Pascal le 2026-10-24 (porte n° 1)
- **Tirée de :** `docs/brief.md`, partie « Deuxième objet » ; décision : ADR 0005, acceptée par Pascal le 2026-10-24
- **Critiquée par :** `contradicteur`, 2026-10-24 (deux passes ; corrections reportées)

## Parcours

1. **Sur le terrain** : le forestier a un modèle qui marche. Il lance une mise à jour (Wi-Fi)
   ou un import par fichier. Si l'opération échoue, s'il l'annule ou si le téléphone s'éteint,
   il garde son modèle, et l'application lui dit pourquoi.
2. **Dans l'application** : le développeur construit
   `InstallationModele(dossier, url, empreinte, espaceDisponible = File::getUsableSpace)`, avec
   `Empreinte.Sha256(hex)` ou `Empreinte.NonControlee`. Il appelle `telecharger` ou
   `installer` hors du fil principal, lit `estInstalle` où il veut, et traduit les erreurs
   typées en messages.

## Données

| Donnée | Forme |
|---|---|
| Dossier du modèle | `dossier` ; à côté, dans le même parent : `<nom>.nouveau`, `<nom>.ancien`, `<nom>.zip` (archive téléchargée), `<nom>.verrou`. Plus rien dans le cache de l'application, qu'Android peut vider. |
| Témoin | fichier `.installe` dans un dossier de modèle ; il contient l'origine. |
| Empreinte | `Empreinte.Sha256(hex)` : 64 caractères hexadécimaux (FIPS 180-4), normalisés en minuscules ; sinon `EmpreinteMalFormee`. La forme est stricte, sans `trim` : un retour à la ligne final est refusé (décisions de Pascal du 2026-10-24). `Empreinte.NonControlee` : refus de contrôler, écrit par l'appelant. Pas de valeur par défaut. |
| Espace disponible | `espaceDisponible: (File) -> Long`, par défaut `File::getUsableSpace`. Sur Android, l'application peut passer `StorageManager.getAllocatableBytes`. Les tests injectent une valeur. |
| Erreurs | exceptions typées, portées par le `Result` : `EmpreinteMalFormee(valeur)`, `EmpreinteInattendue(attendue, obtenue)`, `ArchiveIllisible(raison)`, `StructureInvalide(manquant)` (`manquant` : chemin relatif du premier fichier obligatoire absent, par exemple `"graph/disambig_tid.int"`), `SansGrammaireDynamique`, `EspaceInsuffisant(manquant)`, `Interrompu`. L'application traduit, et les tests vérifient le type et les champs. |
| Vocabulaire | nouvelle fonction `VocabulaireVosk.lireVocabulaire(dossier, candidats)` qui rend `Connus(mots)` ou `Illisible(raison)`. `motsConnus` est inchangé. |

`InstallationModele.empreinte(InputStream)` reste publique, car Surculus l'appelle.

## Comportement

### Modèle valide

Un modèle est valide s'il contient `am/final.mdl`, `conf/model.conf`, `graph/HCLr.fst`,
`graph/Gr.fst` et `graph/disambig_tid.int` (sans ce dernier, Vosk s'arrête en grammaire
dynamique : `vosk-api/src/model.cc`, commit `4ab9821`, l. 298-300 ; décision de Pascal du
2026-10-24). Un fichier vide n'est pas une faute : `ivector/online_cmvn.conf` fait 0 octet
dans le vrai modèle (`docs/referentiels/modele-vosk.md`). Un modèle qui contient `graph/HCLG.fst`, avec ou sans `HCLr.fst`, est refusé par
`SansGrammaireDynamique` (Vosk charge `HCLG.fst` dès qu'il existe ; décision de Pascal du
2026-10-24) : Vosk n'y applique pas de grammaire (« Runtime graphs are not
supported by this model », `vosk-api/src/recognizer.cc`), et terrain ne sert que la grammaire
fermée (décision de Pascal du 2026-10-24). Ces fichiers sont nécessaires, pas suffisants : un
modèle qui demande `ivector/` échouera encore au chargement.

**Racine de l'archive** : c'est le dossier le plus haut qui contient `am/final.mdl` et
`conf/model.conf`. On ignore `__MACOSX/` et `.DS_Store`. La racine peut être celle du zip
(archive sans dossier de tête). On n'installe que ce qui est sous cette racine.

### Reprise (table d'états)

`D` = `dossier`, `N` = `<nom>.nouveau`, `A` = `<nom>.ancien`. Chacun peut être absent, présent
sans témoin, ou présent avec témoin. La reprise s'applique dans cet ordre, la première règle
qui s'applique l'emporte :

| # | État trouvé | Action de reprise | `estInstalle` avant reprise |
|---|---|---|---|
| R1 | `D` avec témoin | effacer `N` et `A` | vrai |
| R2 | `D` sans témoin ou absent, `N` avec témoin | **avancer** : effacer `D`, renommer `N` → `D`, effacer `A` (décision de Pascal du 2026-10-24) | vrai |
| R3 | `D` sans témoin ou absent, `N` sans témoin ou absent, `A` avec témoin | **reculer** : effacer `D` et `N`, renommer `A` → `D` | vrai |
| R4 | aucun témoin nulle part | effacer `D`, `N` et `A` | faux |

- **Le constructeur n'écrit rien** : ni le dossier parent, ni le verrou, qui n'est créé qu'au
  début de `installer`, `telecharger` ou `supprimer` (décision de Pascal du 2026-10-24).
- **`estInstalle` n'écrit jamais.** C'est une lecture pure, sûre sur le fil principal. Elle
  rend vrai en R1, R2 et R3, puisque le modèle y est récupérable.
- **La reprise se fait au début de `installer`, `telecharger` et `supprimer`**, sous le
  verrou.
- Pour invalider un dossier, on efface **toujours d'abord son témoin**, puis le reste. Un
  effacement interrompu ne laisse donc pas de dossier à témoin incomplet.

### Ordre des opérations de `installer(archive)`

Le premier contrôle qui échoue donne son erreur. Tout échec avant l'étape 11 laisse `D`
intact. Si l'échec survient entre 10 et 11, `A` est renommé en `D`. `N` est effacé, témoin en
premier.

1. Prendre le verrou (dans l'instance et `FileChannel.lock` sur `<nom>.verrou`), puis
   appliquer la reprise.
2. Ouvrir l'archive par son répertoire central (`java.util.zip.ZipFile`). Un fichier qui
   n'est pas un zip, ou qui est tronqué, donne `ArchiveIllisible`. Un zip sans entrée utile
   donne `StructureInvalide`.
3. Avec `Empreinte.Sha256`, calculer l'empreinte de l'archive. Un écart donne
   `EmpreinteInattendue(attendue, obtenue)`.
4. Trouver la racine, puis contrôler la structure : `StructureInvalide(manquant)` ou
   `SansGrammaireDynamique`.
5. Faire la somme des tailles décompressées du répertoire central, sous la racine.
6. Vérifier que `espaceDisponible(parent)` est au moins égal à cette somme plus la marge. Sinon,
   `EspaceInsuffisant(manquant)`, et rien n'est effacé.
7. Effacer un `N` resté là, puis décompresser dans `N` en comptant les octets écrits. Au-delà
   de la taille déclarée, `ArchiveIllisible`. Toute entrée qui sort de `N` est refusée.
   Toute entrée dont le chemin contient un segment `..`, même s'il reste sous la racine,
   donne `ArchiveIllisible(raison)` (décision de Pascal du 2026-10-24).
   `continuer` est consulté pendant la décompression : s'il devient faux, `Interrompu`.
8. Forcer l'écriture des fichiers sur le disque (`FileChannel.force(true)`), écrire le témoin
   dans `N`, puis forcer à nouveau.
9. Effacer un `A` resté là.
10. Si `D` existe, le renommer en `A` (`Files.move`, sans `REPLACE_EXISTING`).
11. Renommer `N` en `D`.
12. Effacer `A`, témoin en premier.

### `telecharger`

1. Prendre le verrou et appliquer la reprise.
2. Si `Content-Length` est connu, vérifier que `espaceDisponible(parent)` est au moins égal à
   cette longueur plus la marge. Sinon, `EspaceInsuffisant`.
3. Télécharger dans `<nom>.zip`. `continuer` est consulté à chaque bloc : s'il devient faux,
   `Interrompu`. Une coupure réseau donne `ArchiveIllisible`.
4. Enchaîner les étapes 2 à 12 d'`installer`, l'étape 6 comptant aussi l'archive.
5. Effacer `<nom>.zip` dans tous les cas. **Sur tout échec, `D` est intact** : le comportement
   change, l'ancienne KDoc disait « Échec → dossier effacé ».

### `supprimer`

Elle prend le verrou, puis efface `D`, `N`, `A` et `<nom>.zip`, chacun témoin en premier.

### Vocabulaire

- `lireVocabulaire` rend `Illisible("pas de Gr.fst")` quand `graph/Gr.fst` manque. Il n'y a
  **pas de repli sur `words.txt`** : sans `Gr.fst`, le modèle n'a pas de grammaire dynamique.
- Elle rend `Illisible(raison)` sur un en-tête ou une table illisible, ou quand `Gr.fst` n'a
  aucune table de symboles, et `Connus(mots)` sinon.
- Vosk lit son lexique dans les symboles de **sortie** de `Gr.fst` (`vosk-api/src/model.cc`),
  alors que `motsConnus` lit ceux d'**entrée**. `lireVocabulaire` lit la table de sortie si
  elle existe, sinon celle d'entrée (décision de Pascal du 2026-10-24). Sur
  `vosk-model-small-fr-0.22`, les deux tables sont identiques (135 774 symboles, mission 016).

## Jeux d'essai

Suivant la convention de `terrain-gpkg`, un script `terrain-voix/src/test/resources/modele/fabriquer.sh`
fabrique avec Info-ZIP (`zip -X`, sans horodatage variable) des archives versionnées. Il écrit
leurs empreintes calculées par `sha256sum` (GNU coreutils) dans `empreintes.txt`. Le code testé
ne calcule pas les empreintes de référence. Les archives sont :

- bonne (avec racine) ;
- bonne sans racine ;
- tronquée ;
- vide (0 octet) ;
- zip sans entrée ;
- racine seule ;
- sans `am/final.mdl` ;
- sans `graph/disambig_tid.int` ;
- avec un fichier vide (comme `ivector/online_cmvn.conf`), installée ;
- à `HCLG.fst` ;
- à `HCLG.fst` et `HCLr.fst` à la fois ;
- avec `__MACOSX/` ;
- avec une entrée qui sort du dossier ;
- avec une entrée `a/../b` qui reste sous la racine.

Les fichiers de modèle y sont des fichiers factices (quelques octets), sauf pour le
vocabulaire, qui garde son jeu actuel.

## Critères d'acceptation

Un test par cas dans `terrain-voix`. Chaque test qui part d'un modèle installé vérifie que
**son contenu** est intact (un fichier de l'ancien présent, un fichier du nouveau absent), pas
seulement le témoin.

1. **Table d'états** : un test par ligne R1 à R4. L'état est fabriqué directement sur le
   disque, puis on appelle `estInstalle` (qui n'écrit rien), puis `installer`, et on vérifie
   l'état d'arrivée.
2. **Archives** : un test par jeu d'essai ci-dessus, avec l'erreur typée attendue. La bonne
   archive sans racine s'installe.
3. **Empreinte** : une empreinte fausse donne `EmpreinteInattendue`, avec l'attendue et
   l'obtenue. Une empreinte vide, de 63 caractères ou avec un caractère non hexadécimal donne
   `EmpreinteMalFormee` à la construction. Une empreinte en majuscules est acceptée.
4. **`telecharger`** : une annulation, une coupure (flux tronqué) et un `Content-Length` trop
   grand pour la place disponible laissent le modèle installé intact. Le test actuel
   « telechargement - par URL, puis interruption » est inversé.
5. **Espace** : avec `espaceDisponible` injecté sous le besoin, on obtient
   `EspaceInsuffisant(manquant)` et rien n'est effacé.
6. **Annulation pendant la décompression** : `Interrompu`, le modèle est intact et `N` est
   absent.
7. **Vocabulaire** : `Gr.fst` absent ou illisible donne `Illisible` ; le jeu actuel donne
   `Connus`.
8. **`estInstalle`** n'écrit rien sur le disque : l'arborescence est la même avant et après.

Les tests ne vérifient pas le type d'exception de `Files.move`, qui dépend du système de
fichiers. `./gradlew test` passe en CI.

**Documentation** : le README décrit le nouveau constructeur et les erreurs typées. La note de
version de v0.7.0 donne :

- **pour Marculus** : le constructeur, qui perd `cache` et gagne `empreinte` ; il faut passer
  `Empreinte.NonControlee` ou une empreinte calculée ;
- **pour Surculus** : les tests à réécrire (`InstallationModeleTest.kt` : paramètre `sha256 =`,
  jeux à `a.txt`, test « ECART » à remplacer par l'archive tronquée), `ModeleVoskImport.kt:31` (constructeur)
  et `ModeleVoskImport.kt:47` (`installer` sans second argument).

## Précisions du plan de la mission 018 (retenues par Pascal le 2026-10-24)

- **Marge d'espace libre** : **32 Mio** (33 554 432 octets), constante, la même pour
  `installer` et `telecharger`.
- **Besoin d'espace** : `installer` demande la somme décompressée + marge ; `telecharger`
  demande `Content-Length` + marge avant le téléchargement, puis la somme décompressée + marge
  à l'étape 6 (l'archive, déjà écrite, est déjà déduite de l'espace libre). Pour
  `vosk-model-small-fr-0.22` : 102 038 239 octets pour `installer`, 144 271 562 octets libres
  à prévoir avant un téléchargement ; pic sur le disque ≈ 3,1 fois le décompressé.
- **`installer(archive, continuer)`** perd le paramètre `origine` : le témoin note le nom de
  l'archive, ou l'URL pour `telecharger`.
- **Erreurs d'entrée-sortie** hors de la liste (disque plein en cours d'écriture, droits,
  renommage refusé) : remontées telles quelles (`IOException`) dans le `Result` ; le modèle en
  place reste garanti.
- **Verrou** partagé par tout le processus, rangé par chemin canonique de `<nom>.verrou`, puis
  `FileChannel.lock` ; il attend, il n'échoue pas. `<nom>.verrou` n'est jamais effacé.
- **CRC-32** de chaque entrée contrôlé pendant la décompression ; un écart donne
  `ArchiveIllisible`.
- **Deux racines candidates** à la même profondeur : `ArchiveIllisible("plusieurs modèles")`.
- **Entrée `.installe` dans l'archive** : ignorée, jamais décompressée ; le seul témoin est
  celui que pose l'étape 8 (décision de Pascal du 2026-10-24, relecture de la mission 018).
- **`reprendre(): Result<Unit>`**, fonction publique ajoutée : prend le verrou et applique la
  reprise R1-R4, sans rien installer. À appeler hors du fil principal avant de charger le
  modèle, pour qu'un état R2 ou R3 laissé par une coupure soit remis en `dossier`. La note de
  version le demande à Marculus et à Surculus (décision de Pascal du 2026-10-24).
- **Étape 12** : si l'effacement de `<nom>.ancien` échoue, `installer` rend un **succès** ; le
  nouveau modèle est en place et la reprise R1 effacera l'ancien à l'opération suivante
  (décision de Pascal du 2026-10-24).
- **`supprimer(): Result<Unit>`** ; les erreurs ont pour parente scellée `ErreurModele` ; les
  noms d'entrées sont refusés juste avant toute écriture (étape 7) ; l'écriture forcée d'un
  dossier est tentée au mieux (choix du `dev` retenus par Pascal le 2026-10-24).
- **Refus rangés sous `ArchiveIllisible`** : nom d'entrée absolu, `..` séparé par `/` ou `\`,
  entrée en double, taille inconnue, somme de tailles qui déborde, et réseau (injoignable,
  erreur HTTP, URL `file:` absente : `ArchiveIllisible("réseau : …")`).

## Questions ouvertes

Aucune.
