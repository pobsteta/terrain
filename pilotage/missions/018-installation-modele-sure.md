# Mission 018 — installation du modèle : l'ancien survit à tout échec

> Fiche de mission (fiches n° 9 et 15 de la méthode). Une mission = une phrase = une
> branche = une PR. Tirée de la spec de `terrain-voix` (porte n° 1 du 2026-10-24, ADR 0005).

## Mission

**En une phrase (quoi, et pourquoi) :** Réécrire `InstallationModele` selon la spec de `terrain-voix` et l'ADR 0005 (`Empreinte`, erreurs typées, verrou, reprise R1-R4, ordre des opérations en 12 étapes, contrôle de structure et d'espace, `estInstalle` sans écriture) jusqu'à faire passer les tests de la mission 017, et mettre à jour README et KDoc, pour qu'un import ou une mise à jour ratés ne retirent plus jamais au forestier un modèle qui marchait.

**Redite par l'agent :** _à compléter par l'agent avant d'agir — s'il n'y arrive pas, il s'arrête._

## Les trois questions (règle de staffing)

| Question | Réponse | Conséquence |
|---|---|---|
| Peut-il redire la mission en une phrase, avec son but ? | oui | — |
| Sait-il comment faire ? (sources, conventions nommées ci-dessous) | oui (spec détaillée, ADR 0005 acceptée) ; la mission change l'API publique | **tenir** : plan d'abord |
| A-t-il les moyens ? (outils, données d'essai, tests) | après 017 (tests) et 016 (taille du modèle, pour la marge) | ne part pas avant |

**Temps de la main :** **tenir** — la mission engage la durée (API publique, v0.7.0, données du forestier). L'agent rend un plan, que l'orchestrateur soumet à Pascal avant toute exécution.

## Ma réponse d'abord

_Rempli par Pascal seul, avant de lire le travail de l'agent : ce qu'il attend de la
mission, et le résultat qu'il prévoit. Une ligne suffit. L'orchestrateur ne le remplit
pas._

## L'Homme

- **Agent :** `dev`
- **Modèle :** opus (bascule, reprise, durabilité : justesse des données du forestier)
- **Ce qui ne lui est pas demandé :** modifier une assertion des tests de 017 (s'il en juge une fausse, il le dit et s'arrête) ; toucher à `VocabulaireVosk` (mission 019) ; poser un tag ; fixer seul la marge.

## Harnais

- **Peut :** réécrire `terrain-voix/src/main/kotlin/fr/nemeton/terrain/voix/InstallationModele.kt` et ajouter les fichiers qu'il faut dans ce paquet (`Empreinte`, erreurs) ; mettre à jour la ligne `terrain-voix` du `README.md` et la partie qui décrit l'installation ; écrire la note de version de v0.7.0 dans le corps de la PR (Marculus : constructeur ; Surculus : tests à réécrire et `ModeleVoskImport.kt:31`) ; pousser sur la branche de 017.
- **Ne doit pas :** toucher à `main`, au journal, aux autres modules ; ajouter une dépendance ; utiliser une API Android (Kotlin pur, JVM 17) ; changer la signature de `motsConnus` ou d'`empreinte(InputStream)`.
- **Sources autorisées :** `docs/spec.md` (partie `terrain-voix`), `docs/adr/0005-installation-modele-vosk.md`, `docs/referentiels/modele-vosk.md` (mission 016), les tests de 017, la Javadoc de Java SE 17 (`Files.move`, `ZipFile`, `FileChannel`, `File.getUsableSpace`).

## Valeur provisoire (chiffre, réglage)

- **Valeur provisoire :** la marge d'espace libre au-delà de la taille décompressée (et de l'archive pour `telecharger`).
- **Mesurée par :** le `dev` la propose dans son plan, à partir de la taille mesurée par la mission 016 ; **Pascal la fixe** (décision du 2026-10-24).
- **Gel :** avant la sortie du brouillon de la PR ; elle est marquée « provisoire » dans le code et la PR jusque-là.

## Dépend de / rouge attendu jusqu'à

- **Dépend de :** 016 (taille du modèle) et 017 (tests) ; part de la branche de 017.
- **Ordre de fusion :** groupée avec 017 et 019.
- **PR en brouillon jusqu'à :** la fin de 019 et la marge fixée par Pascal.
- **CI rouge attendue :** les tests de vocabulaire de 017 (`lireVocabulaire`) ne compilent pas jusqu'à 019. Si la compilation d'un seul fichier de test bloque tout `terrain-voix`, le dire : la CI reste rouge jusqu'à 019, rien d'autre.

## Tests

- **Comment saura-t-on que c'est juste :** les tests de 017 sur l'installation passent sans modification ; `./gradlew test` vert pour les autres modules ; aucun appel à `deleteRecursively` sur un dossier de modèle sans effacer son témoin d'abord (grep dans la PR) ; README et KDoc ne disent plus « Échec → dossier effacé ».

## Terrain

- **Échantillon que Pascal vérifiera :** dans Marculus, après la montée en v0.7.0 : lancer une mise à jour du modèle, couper le Wi-Fi au milieu ; la dictée marche encore.

## Qui veille, qui tranche

- **Relecteur :** `relecteur`, puis Pascal.
- **Critère d'arrêt de la relecture :** 3 allers-retours ; au-delà, la décision remonte à
  Pascal.
- **Point de suivi :** au plan (avant exécution), puis à la PR.
- **Qui tranche :** Pascal.

## Comment saura-t-on que ça marche ?

Surculus retire de son garde-fou le contrôle du vide et remplace son test « ECART terrain-voix » par l'archive tronquée, à la montée en v0.7.0.
