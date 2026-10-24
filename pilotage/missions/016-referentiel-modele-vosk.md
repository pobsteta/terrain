# Mission 016 — référentiel du modèle Vosk small-fr-0.22

> Fiche de mission (fiches n° 9 et 15 de la méthode). Une mission = une phrase = une
> branche = une PR. Tirée de la spec de `terrain-voix` (porte n° 1 du 2026-10-24, ADR 0005).

## Mission

**En une phrase (quoi, et pourquoi) :** Relever sur la vraie archive `vosk-model-small-fr-0.22` sa liste de fichiers, sa taille compressée et décompressée, son empreinte SHA-256 calculée par `sha256sum`, et dire si les tables de symboles d'entrée et de sortie de `graph/Gr.fst` sont identiques, dans `docs/referentiels/modele-vosk.md`, pour que le testeur, le dev et Pascal (marge d'espace) travaillent sur des faits mesurés.

**Redite par l'agent :** _à compléter par l'agent avant d'agir — s'il n'y arrive pas, il s'arrête._

## Les trois questions (règle de staffing)

| Question | Réponse | Conséquence |
|---|---|---|
| Peut-il redire la mission en une phrase, avec son but ? | oui | — |
| Sait-il comment faire ? (sources, conventions nommées ci-dessous) | oui (outils tiers nommés : `curl`, `sha256sum`, `unzip -l` ou `zipinfo`, `du`) | — |
| A-t-il les moyens ? (outils, données d'essai, tests) | oui, s'il a le réseau (41 Mo à télécharger) ; sinon il s'arrête et le dit | — |

**Temps de la main :** appuyer.

## Ma réponse d'abord

_Rempli par Pascal seul, avant de lire le travail de l'agent : ce qu'il attend de la
mission, et le résultat qu'il prévoit. Une ligne suffit. L'orchestrateur ne le remplit
pas._

## L'Homme

- **Agent :** `dev` (il lui faut un shell ; l'`acquereur` n'en a pas)
- **Modèle :** sonnet
- **Ce qui ne lui est pas demandé :** écrire ou modifier du code ou des tests ; versionner l'archive ou le modèle décompressé ; proposer la marge (c'est la mission 018).

## Harnais

- **Peut :** télécharger l'archive depuis l'URL publiée par alphacephei dans un dossier temporaire hors du dépôt ; écrire `docs/referentiels/modele-vosk.md` (nouveau) ; ouvrir une PR en brouillon.
- **Ne doit pas :** toucher à `main`, au code, aux tests, au journal ; inventer une valeur ; versionner un binaire.
- **Sources autorisées :** https://alphacephei.com/vosk/models ; https://github.com/alphacep/vosk-api (`src/model.cc`, `src/recognizer.cc`) ; `terrain-voix/src/main/kotlin/fr/nemeton/terrain/voix/VocabulaireVosk.kt` (format lu par terrain : en-tête OpenFst, drapeaux) ; spec de `terrain-voix` et ADR 0005.

## Valeur provisoire (chiffre, réglage)

Aucune : la mission mesure. Chaque chiffre est donné avec l'outil et la commande qui l'ont produit, et la date du téléchargement.

## Dépend de / rouge attendu jusqu'à

Dépend de : rien. CI rouge attendue : aucune.

## Tests

- **Comment saura-t-on que c'est juste :** chaque chiffre du référentiel a sa commande, rejouable par un successeur ; la liste des fichiers dit si `am/final.mdl`, `conf/model.conf`, `graph/HCLr.fst`, `graph/Gr.fst`, `ivector/` et `graph/words.txt` sont présents ; la comparaison des tables d'entrée et de sortie de `Gr.fst` dit comment elle a été faite (outil tiers si possible, par exemple `fstprint` ou `fstsymbols` d'OpenFst, sinon un script hors du dépôt, dit tel quel) ; `./gradlew test` vert.

## Terrain

- **Échantillon que Pascal vérifiera :** la taille décompressée : est-elle compatible avec l'espace libre d'un téléphone de terrain pendant une mise à jour (trois fois la taille) ?

## Qui veille, qui tranche

- **Relecteur :** `relecteur`, puis Pascal.
- **Critère d'arrêt de la relecture :** 3 allers-retours ; au-delà, la décision remonte à
  Pascal.
- **Point de suivi :** à la PR.
- **Qui tranche :** Pascal.

## Comment saura-t-on que ça marche ?

Le dev de la mission 018 propose la marge à partir de la taille mesurée ici, sans rien deviner.

## Remarques du relecteur (aller-retour 1/3, 2026-10-24)

Verdict de la relecture 1 : prêt. Toutes les mesures ont été rejouées et sont identiques, et l'empreinte est égale à celle de Surculus. Il reste des retouches :

1. **`graph/disambig_tid.int`** : écris que Vosk s'arrête sans ce fichier en grammaire dynamique (`model.cc`, commit `4ab9821`, l. 298-300). Pascal l'a ajouté le 2026-10-24 aux fichiers obligatoires de la spec. Dis aussi que `ivector/` n'est chargé que si `ivector/final.ie` existe (l. 258-275).
2. **Intitulé du tableau** : remplace « fichiers que terrain-voix attend (ADR 0005) » par « fichiers que la fiche demande de vérifier ». `ivector/` et `graph/words.txt` viennent de la fiche, pas de l'ADR.
3. **§7 « Rejouer »** : donne des chemins absolus séparés pour le venv, `cmp.py` et le modèle extrait, sans lancer de script depuis le dossier extrait.
4. **Symboles** : précise que les 135 774 symboles comprennent des symboles spéciaux (`<eps>`, `#0`, `<s>`, `</s>`, `[unk]`).
