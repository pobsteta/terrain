# Mission 017 — tests d'acceptation de l'installation du modèle

> Fiche de mission (fiches n° 9 et 15 de la méthode). Une mission = une phrase = une
> branche = une PR. Tirée de la spec de `terrain-voix` (porte n° 1 du 2026-10-24, ADR 0005).

## Mission

**En une phrase (quoi, et pourquoi) :** Écrire, sans lire l'implémentation, les jeux d'essai figés (`terrain-voix/src/test/resources/modele/fabriquer.sh`, archives et `empreintes.txt`) et les tests d'acceptation 1 à 8 de la spec de `terrain-voix` (table d'états R1-R4, archives, empreinte, `telecharger`, espace, annulation, vocabulaire, `estInstalle` sans écriture), pour que la correction d'`InstallationModele` et de `VocabulaireVosk` soit jugée par des tests qu'elle n'a pas écrits.

**Redite par l'agent :** _à compléter par l'agent avant d'agir — s'il n'y arrive pas, il s'arrête._

## Les trois questions (règle de staffing)

| Question | Réponse | Conséquence |
|---|---|---|
| Peut-il redire la mission en une phrase, avec son but ? | oui | — |
| Sait-il comment faire ? (sources, conventions nommées ci-dessous) | oui (spec, API nommée ; convention `terrain-gpkg/src/test/resources/gpkg/fabriquer.sh`) | — |
| A-t-il les moyens ? (outils, données d'essai, tests) | oui (`zip` Info-ZIP, `sha256sum`) ; il les fabrique | — |

**Temps de la main :** appuyer.

## Ma réponse d'abord

_Rempli par Pascal seul, avant de lire le travail de l'agent : ce qu'il attend de la
mission, et le résultat qu'il prévoit. Une ligne suffit. L'orchestrateur ne le remplit
pas._

## L'Homme

- **Agent :** `testeur`
- **Modèle :** sonnet
- **Ce qui ne lui est pas demandé :** écrire ou corriger l'implémentation ; lire `InstallationModele.kt` ou `VocabulaireVosk.kt` (seules leurs signatures publiques actuelles, par la spec et le README) ; trancher une question de valeur.

## Harnais

- **Peut :** écrire `terrain-voix/src/test/resources/modele/` (script, archives, `empreintes.txt`) ; réécrire `terrain-voix/src/test/kotlin/fr/nemeton/terrain/voix/InstallationModeleTest.kt` et ajouter des tests à `VocabulaireVoskTest.kt` ; inverser le test « telechargement - par URL, puis interruption » ; ouvrir une PR en brouillon.
- **Ne doit pas :** toucher au code de `src/main`, à `main`, au journal ; calculer une empreinte de référence avec le code testé ; affirmer le type d'exception de `Files.move` ; inventer une API absente de la spec (s'il en manque une, la nommer dans le rendu).
- **Sources autorisées :** `docs/spec.md` (partie `terrain-voix`), `docs/adr/0005-installation-modele-vosk.md`, `README.md`, `terrain-gpkg/src/test/resources/gpkg/fabriquer.sh` (convention), les tests existants de `terrain-voix`.

## Valeur provisoire (chiffre, réglage)

La marge d'espace libre n'est pas encore fixée (mission 018, puis Pascal) : les tests d'espace injectent `espaceDisponible` très au-dessous ou très au-dessus du besoin, sans dépendre de la valeur de la marge.

## Dépend de / rouge attendu jusqu'à

- **Dépend de :** rien ; part de `main`.
- **Ordre de fusion :** groupée avec 018 et 019 (017 → 018 → 019, fusion des trois ensemble).
- **PR en brouillon jusqu'à :** la fin de la mission 019.
- **CI rouge attendue :** `./gradlew test` ne compile pas (`Empreinte`, erreurs typées, `lireVocabulaire`, nouveau constructeur absents) jusqu'à la mission 018 pour `InstallationModele` et 019 pour le vocabulaire. Tout autre rouge est un écart.

## Tests

- **Comment saura-t-on que c'est juste :** chaque critère 1 à 8 de la spec a au moins un test nommé d'après lui ; chaque ligne R1-R4 a son test, l'état étant fabriqué sur le disque ; chaque test qui part d'un modèle installé vérifie son contenu (un fichier de l'ancien présent, un du nouveau absent) ; `fabriquer.sh`, rejoué, redonne les mêmes empreintes (`sha256sum -c empreintes.txt`) ; la seule erreur de compilation est celle annoncée ci-dessus.

## Terrain

- **Échantillon que Pascal vérifiera :** le nom des tests de la table d'états : y lit-il, sans le code, ce que garde le forestier après chaque crash ?

## Qui veille, qui tranche

- **Relecteur :** `relecteur`, puis Pascal.
- **Critère d'arrêt de la relecture :** 3 allers-retours ; au-delà, la décision remonte à
  Pascal.
- **Point de suivi :** à la PR.
- **Qui tranche :** Pascal.

## Comment saura-t-on que ça marche ?

Les missions 018 et 019 passent au vert sans modifier une assertion de cette mission.

## Remarques du relecteur (aller-retour 1/3, 2026-10-24)

Verdict de la relecture 1 : prêt, avec des retouches. Pascal a tranché, le 2026-10-24, les questions du testeur et du relecteur (spec mise à jour sur `origin/pilotage/brief-voix`) :

- **`graph/disambig_tid.int` est obligatoire** : ajoute-le à toutes les bonnes archives. Ajoute aussi une archive `sans-disambig.zip`, qui donne `StructureInvalide`.
- **Vocabulaire** : on lit la table de sortie si elle existe, sinon celle d'entrée. Les tests restent tels quels. Mets à jour le commentaire qui parle de « question ouverte » : la mission 016 a mesuré les deux tables, et elles sont identiques.
- **Entrée évadée** : toute entrée qui sort du dossier, ou dont le chemin contient `..` (même `a/../b`, qui reste sous la racine), donne `ArchiveIllisible`. Resserre le test d'évasion sur ce type. Ajoute une archive `point-point-interne.zip` (`racine/a/../b`) qui donne `ArchiveIllisible`. Le test doit nettoyer, avant et après, le fichier qu'il traque dans `/tmp`.
- **Constructeur** : il n'écrit rien sur le disque. Garde la photo prise avant la construction.

Retouches du relecteur :
1. `racine-seule.zip` doit contenir exactement le dossier racine, et rien d'autre.
2. Ajoute une bonne archive qui contient un fichier vide (comme `ivector/online_cmvn.conf` dans le vrai modèle). Elle doit s'installer.
3. Reporte dans le corps de la PR, comme contrat pour les missions 018 et 019, les noms et types que tu as supposés : paquet, types de premier niveau, champs et leurs types, signature d'`installer` avec `continuer`.
4. Relance `fabriquer.sh`, `sha256sum -c` et le comptage des tests, puis rends les nouveaux chiffres.

Liste de contrôle jointe de nouveau.

## Remarques du relecteur (aller-retour 2/3, 2026-10-24)

Verdict de la relecture 2 : prêt. Pascal a tranché le 2026-10-24 (spec à jour) :

- **`StructureInvalide.manquant`** contient le chemin relatif du premier fichier obligatoire absent, par exemple `"graph/disambig_tid.int"`. Vérifie-le dans chaque test `StructureInvalide` où un seul fichier manque (sans `am/final.mdl`, sans `disambig_tid.int`).
- **Empreinte suivie d'un retour à la ligne** : elle est refusée, sans `trim`. Ton test reste tel quel.
- **`Gr.fst` sans table de symboles** : `Illisible`. Ton test reste tel quel.

Retouches mineures du relecteur :
1. `InstallationModeleTest.kt`, l. 619 : le commentaire est inversé. `continuer = { !nouveau.exists() }` est **vrai** tant que `modele.nouveau` n'existe pas.
2. Contrôle la mise en minuscules : une empreinte fausse donnée en majuscules doit donner `attendue` en minuscules.

Relance le comptage des tests et `sha256sum -c`. Liste de contrôle jointe de nouveau.
