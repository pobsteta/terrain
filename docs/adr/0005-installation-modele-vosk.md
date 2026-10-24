# ADR 0005 — installation du modèle Vosk : l'ancien modèle survit à tout échec

- **Date :** 2026-10-24
- **Statut :** acceptée par Pascal le 2026-10-24 (« Prêts à payer ? » : inconnu)
- **Proposée par :** Claude (orchestrateur), sur le brief de Surculus (issue
  [#23](https://github.com/pobsteta/terrain/issues/23)) et la critique du `contradicteur`
- **Tranchée par :** Pascal

## La décision en une phrase

`InstallationModele` ne touche plus au modèle en place tant que le nouveau n'est pas
décompressé, vérifié et prêt. L'empreinte devient un choix explicite de l'appelant. Le
vocabulaire gagne une lecture qui distingue « illisible » de « connu ». Un modèle n'est installé que s'il porte
une grammaire dynamique. Le tout est publié en **v0.7.0**, parce que la signature du
constructeur change.

## Contexte

Le brief de Surculus du 2026-10-24
(`surculus/pilotage/briefs/vers-terrain/2026-10-24-ecarts-terrain-voix.md`) relève quatre
écarts d'`InstallationModele` et un choix de `VocabulaireVosk`. Ils sont vérifiés dans le code
de v0.6.0 (`d840329`) par le `contradicteur`, et ce code n'a pas changé depuis sur `main`. Les
voici :

1. **Effacement avant décompression.** `installer` efface le dossier avant de décompresser et
   sur tout échec. `telecharger` l'efface aussi sur échec, y compris sur une coupure réseau ou
   une annulation (`check(continuer())`). Un import ou une mise à jour ratés détruisent donc un
   modèle qui marchait, et le forestier le découvre en forêt, hors réseau.
2. **Empreinte facultative.** Avec `sha256 = null`, la valeur par défaut, l'empreinte n'est pas
   contrôlée. Marculus n'en passe aucune (`ModeleVosk.kt:29`). Alphacephei ne publie aucune
   empreinte pour ses archives : celle de Surculus a été calculée par Surculus.
3. **Archive vide acceptée.** Le témoin atteste « décompression finie », pas « modèle valide ».
   De plus, `decompresser` retire toujours le premier élément du chemin : une archive refaite
   sans dossier racine installe un modèle cassé.
4. **Forme de l'empreinte non contrôlée.** Une empreinte vide ou mal formée n'est refusée que
   parce que la comparaison échoue, et le message n'affiche que l'empreinte obtenue.
5. **Vocabulaire illisible.** `motsConnus` rend tous les candidats comme connus quand
   `graph/Gr.fst` est absent ou illisible. C'est un choix documenté, qui laisse passer plutôt que
   de bloquer.

Sur la JVM, un dossier non vide ne se remplace pas d'un coup : `Files.move` lève
`DirectoryNotEmptyException`, et `rename(2)` rend `ENOTEMPTY`. Il faut donc deux renommages, et
une reprise si le processus est tué entre les deux, ce qu'Android peut faire sans exécuter de
`finally`.

**Options envisagées :**

- **A.** Garder le comportement actuel et l'écrire. Surculus garde son garde-fou.
- **B.** Corriger sans changer de signature (v0.6.1). L'empreinte reste facultative.
- **C.** Corriger et rendre l'empreinte explicite (v0.7.0). *Retenue par Pascal le 2026-10-24.*
- **D.** C, plus un catalogue des modèles dans terrain (URL et empreinte, une seule source pour
  les applications). Non retenue pour l'instant.

## Ce qui est décidé (arbitrages de Pascal du 2026-10-24)

Le détail (table d'états, ordre des opérations, erreurs, jeux d'essai) est dans
`docs/spec.md`, partie `terrain-voix`. Ce qui suit en est l'essentiel.

- **Échec : l'ancien modèle est gardé.**
  - On décompresse dans `<nom>.nouveau`, à côté du dossier et jamais dans le cache.
  - On vérifie le contenu, on force l'écriture sur le disque, puis on pose le témoin.
  - On bascule en deux renommages : l'ancien vers `<nom>.ancien`, puis le nouveau vers le
    dossier.
  - Une reprise, au début de chaque opération et sous verrou, applique une table d'états
    écrite. Si le nouveau est vérifié, elle **avance** ; sinon, elle remet l'ancien en place
    (décision de Pascal du 2026-10-24).
  - `estInstalle` n'écrit jamais : Marculus l'appelle depuis l'interface.
  - Le test de terrain et la KDoc « Échec → dossier effacé » sont **inversés**.
- **Disque plein : la mise à jour est refusée.**
  - Le contrôle se fait par une fonction injectable (`File::getUsableSpace` par défaut,
    `getAllocatableBytes` sur Android).
  - Il a lieu avant le téléchargement (`Content-Length`) et avant la décompression (somme des
    tailles du répertoire central).
  - Les octets réellement écrits sont comptés.
  - La marge est fixée par Pascal le 2026-10-24 à 32 Mio (plan de la mission 018).
- **Empreinte : choix explicite.**
  - `Empreinte.Sha256(hex)` : forme contrôlée à la construction (64 caractères hexadécimaux,
    FIPS 180-4), normalisée en minuscules.
  - `Empreinte.NonControlee` : un refus de contrôler, écrit par l'appelant.
  - Il n'y a pas de valeur par défaut. En cas d'écart, l'erreur donne l'empreinte attendue et
    l'empreinte obtenue.
  - Coût : Marculus change une ligne.
- **Structure : un modèle à grammaire dynamique, et rien d'autre.**
  - Un modèle valide a `am/final.mdl`, `conf/model.conf`, `graph/HCLr.fst`, `graph/Gr.fst` et
    `graph/disambig_tid.int` (ajouté le 2026-10-24, sur la relecture de la mission 016).
  - Un modèle à `HCLG.fst` est refusé (`SansGrammaireDynamique`) : Vosk n'y applique pas de
    grammaire, et terrain ne sert que la grammaire fermée (décision de Pascal du 2026-10-24).
  - La racine de l'archive est détectée, en ignorant `__MACOSX/`. Une archive sans dossier de
    tête est installée si sa structure est bonne.
- **Vocabulaire : ajout d'une fonction.**
  - `motsConnus` reste tel quel.
  - `lireVocabulaire` rend `Connus(mots)` ou `Illisible(raison)`, sans repli sur `words.txt`.
- **Erreurs typées**, sur le modèle de `SystemeNonGere` : l'application traduit, et le test
  vérifie le type.
- **Jeux d'essai figés**, fabriqués par Info-ZIP, avec des empreintes calculées par
  `sha256sum`, sur le modèle de `terrain-gpkg/src/test/resources/gpkg/fabriquer.sh`.
- **Version : v0.7.0.**
  - La signature du constructeur change : `cache` disparaît, `empreinte` devient obligatoire.
  - La règle 4 de SemVer ne fonde rien ici : en 0.y.z, elle permet tout.
  - Ce qui fonde le choix, c'est l'usage de Cargo (le chiffre du milieu d'une 0.x vaut un
    majeur) et le précédent de terrain (v0.1.2 → v0.2.0).

## Le crible FRANCE

| Lettre | Question | Réponse (une ligne) | Ce que ça coûte | Prêts à payer ? |
|---|---|---|---|---|
| **F**rugale | La plus simple qui tienne ? | deux renommages, une table de reprise à quatre lignes, un type à deux formes pour l'empreinte, en Kotlin pur | une vingtaine de tests et un script de jeux d'essai | inconnu |
| **R**esponsable | Qui fait quoi ? | terrain garantit la bascule, la reprise et la structure ; chaque appelant choisit son empreinte et sa mesure de l'espace | Marculus calcule une empreinte, ou écrit `NonControlee` | inconnu |
| **A**nticiper | Et si l'imprévu arrive ? | processus tué ou batterie vide : reprise et écriture forcée ; disque plein : refus ; archive tronquée, piégée ou à `HCLG` : refus ; archive sans dossier de tête : installée si la structure y est | environ trois fois la taille du modèle sur le disque pendant la mise à jour (non mesuré) | inconnu |
| **N**ovateur | Toutes les idées ? | quatre options, dont le catalogue de modèles (D, écartée pour l'instant) ; avancer ou reculer à la reprise | — | — |
| **C**oût | Dépense inutile ? | pas de dépendance nouvelle | Marculus, encore en v0.5.0, absorbe aussi v0.6.0 ; Surculus réécrit six tests | inconnu |
| **É**cologique | Réutilisation ? | Surculus allège son garde-fou (`VerificationModele.kt` : il garde son empreinte, et le contrôle du vide devient inutile) ; Marculus en profite | — | inconnu |

## Valeurs sacrifiées

- **Compatibilité ascendante.** Le constructeur change pour Marculus, et six tests de Surculus
  sont à réécrire. On l'accepte : un oubli d'empreinte ne doit plus passer en silence.
- **Une seule source pour l'empreinte.** Sans catalogue (option D), chaque application tient la
  sienne.
- **Capacité sur un disque presque plein.** On refuse une mise à jour qu'on aurait pu faire en
  effaçant d'abord l'ancien modèle.
- **Gros modèles en dictée libre.** Les modèles à `HCLG.fst` sont refusés : ce serait un autre
  usage, à décider à part.
- **Simplicité.** La reprise demande une table d'états et un verrou. En échange, `estInstalle`
  reste une lecture pure.

## Sources

- Brief de Surculus du 2026-10-24, issue #23 ; code de `terrain-voix` à `d840329` (v0.6.0,
  inchangé depuis sur `main`).
- Critique du `contradicteur` (deux passes, 2026-10-24), dans `pilotage/journal.md`.
- alphacep/vosk-api, `src/recognizer.cc`, l. 58-61 (« Runtime graphs are not supported by this
  model ») et `src/model.cc` — https://github.com/alphacep/vosk-api
- Alphacephei, « Vosk Models » (structure d'un modèle ; aucune empreinte publiée) —
  https://alphacephei.com/vosk/models
- Oracle, Javadoc Java SE 17 : `java.nio.file.Files.move`, `java.io.File.getUsableSpace`
  (« a hint, but not a guarantee »), `java.util.zip.ZipEntry.getSize` (-1 si inconnu) —
  https://docs.oracle.com/en/java/javase/17/docs/api/java.base/
- man7.org, `rename(2)` — https://man7.org/linux/man-pages/man2/rename.2.html
- Android Developers, « Access app-specific files » —
  https://developer.android.com/training/data-storage/app-specific
- NIST FIPS 180-4, Secure Hash Standard — https://csrc.nist.gov/pubs/fips/180-4/upd1/final
- SemVer 2.0.0, règle 4 — https://semver.org/lang/fr/ ; The Cargo Book, « SemVer
  compatibility » — https://doc.rust-lang.org/cargo/reference/semver.html (non relu)
- LWN, « ext4 and data loss » — https://lwn.net/Articles/322823/ (non relu)

## Question de contrôle

Cette décision tient-elle nos six valeurs, et que sommes-nous prêts à payer pour cela ?
