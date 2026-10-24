# ADR 0005 — installation du modèle Vosk : l'ancien modèle survit à tout échec

- **Date :** 2026-10-24
- **Statut :** proposée (arbitrages de Pascal du 2026-10-24 reportés ; porte n° 1 à passer)
- **Proposée par :** Claude (orchestrateur), sur le brief de Surculus (issue
  [#23](https://github.com/pobsteta/terrain/issues/23)) et la critique du `contradicteur`
- **Tranchée par :** Pascal

## La décision en une phrase

`InstallationModele` ne touche plus au modèle en place tant que le nouveau n'est pas
décompressé, vérifié et prêt. L'empreinte devient un choix explicite de l'appelant. Le
vocabulaire gagne une lecture qui distingue « illisible » de « connu ». Le tout est publié en
**v0.7.0**, parce que la signature du constructeur change.

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

- **Échec : l'ancien modèle est gardé.**
  - La décompression se fait dans un dossier à côté (`<dossier>.nouveau`, sur le même système
    de fichiers que le dossier cible, jamais dans le cache).
  - Le contenu est vérifié, puis le témoin est posé dans ce dossier.
  - La bascule se fait en deux renommages : l'ancien vers `<dossier>.ancien`, puis le nouveau
    vers `<dossier>`. On efface ensuite `.ancien`.
  - Une reprise remet l'ancien en place si le processus a été tué entre les deux renommages,
    puis nettoie les restes.
  - Le test de terrain « telechargement - par URL, puis interruption » et la KDoc « Échec →
    dossier effacé » sont **inversés**. C'est un changement de comportement documenté : Marculus
    est prévenu par la note de version.
- **Disque plein : la mise à jour est refusée.** Avant de commencer, l'espace libre est contrôlé
  (`File.usableSpace`). S'il ne suffit pas, le modèle en place est gardé, et le message dit
  l'espace manquant.
- **Empreinte : choix explicite.**
  - Le paramètre `sha256: String? = null` est remplacé par un type `Empreinte` à deux formes :
    - `Empreinte.Sha256(hex)` : la forme est contrôlée à la construction (64 caractères
      hexadécimaux, FIPS 180-4), avec un message qui le dit ;
    - `Empreinte.NonControlee` : un refus de contrôler écrit en toutes lettres par l'appelant.
  - Il n'y a pas de valeur par défaut. Le message de refus donne l'empreinte attendue **et**
    l'empreinte obtenue.
  - Coût : Marculus change une ligne à la montée de version.
- **Vocabulaire : ajout d'une fonction.**
  - `motsConnus` reste tel quel, pour ne pas casser Marculus (`DicteeVocale.kt:252`).
  - Une nouvelle fonction rend « connus » ou « illisible (raison) ». Elle lit `graph/words.txt`
    (format texte de Kaldi) quand `Gr.fst` manque.
- **Archive vide et structure du modèle** (corrections techniques proposées par le
  `contradicteur`, à valider à la porte n° 1) :
  - Avant la bascule, on contrôle la structure publiée par alphacephei : `am/final.mdl`,
    `conf/model.conf`, et `graph/HCLG.fst` ou bien `graph/HCLr.fst` avec `graph/Gr.fst`.
  - Cela couvre l'archive vide, le zip sans entrée et le dossier racine absent.
  - Le dossier racine de l'archive est détecté, et non plus supposé.
- **Version : v0.7.0.** En 0.y.z, le chiffre du milieu marque un changement incompatible :
  c'est l'usage de Cargo, et le précédent de terrain (v0.1.2 → v0.2.0).

## Le crible FRANCE

| Lettre | Question | Réponse (une ligne) | Ce que ça coûte | Prêts à payer ? |
|---|---|---|---|---|
| **F**rugale | La plus simple qui tienne ? | deux renommages et une reprise, en Kotlin pur ; un type à deux formes pour l'empreinte | plus de code et six tests de plus dans `InstallationModele` | inconnu |
| **R**esponsable | Qui fait quoi ? | terrain garantit la bascule et la structure ; chaque appelant choisit et tient son empreinte | Marculus doit calculer une empreinte, ou écrire `NonControlee` | inconnu |
| **A**nticiper | Et si l'imprévu arrive ? | crash entre les renommages : reprise ; disque plein : refus ; archive refaite sans racine : refus | environ trois fois la taille du modèle sur le disque pendant la mise à jour | inconnu |
| **N**ovateur | Toutes les idées ? | quatre options, dont le catalogue de modèles (D, écartée pour l'instant) | — | — |
| **C**oût | Dépense inutile ? | pas de dépendance nouvelle | Marculus, encore en v0.5.0, absorbe aussi la v0.6.0 à la montée | inconnu |
| **É**cologique | Réutilisation ? | Surculus pourra retirer son garde-fou (`VerificationModele.kt`) ; Marculus en profite | — | inconnu |

## Valeurs sacrifiées

- **Compatibilité ascendante pour Marculus.** Le constructeur change : on l'accepte, parce qu'un
  oubli d'empreinte ne doit plus passer en silence.
- **Une seule source pour l'empreinte.** Sans catalogue (option D), chaque application tient la
  sienne. Si deux applications prennent le même modèle, la même valeur existe à deux endroits.
- **Capacité sur disque presque plein.** On refuse une mise à jour qu'on aurait pu faire en
  effaçant d'abord l'ancien modèle.

## Sources

- Brief de Surculus du 2026-10-24, issue #23 ; code de `terrain-voix` à `d840329`.
- Oracle, Javadoc Java SE 17, `java.nio.file.Files.move` —
  https://docs.oracle.com/en/java/javase/17/docs/api/java.base/java/nio/file/Files.html
- man7.org, `rename(2)` — https://man7.org/linux/man-pages/man2/rename.2.html
- Android Developers, « Access app-specific files » (le cache peut être effacé ; contrôler
  l'espace libre) — https://developer.android.com/training/data-storage/app-specific
- Alphacephei, « Vosk Models » (structure d'un modèle ; aucune empreinte publiée) —
  https://alphacephei.com/vosk/models
- NIST FIPS 180-4, Secure Hash Standard — https://csrc.nist.gov/pubs/fips/180-4/upd1/final
- SemVer 2.0.0, règle 4 — https://semver.org/lang/fr/ ; The Cargo Book, « SemVer
  compatibility » — https://doc.rust-lang.org/cargo/reference/semver.html (non relu)

## Question de contrôle

Cette décision tient-elle nos six valeurs, et que sommes-nous prêts à payer pour cela ?
