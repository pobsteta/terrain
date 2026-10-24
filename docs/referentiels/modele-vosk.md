# Référentiel — modèle Vosk `vosk-model-small-fr-0.22` (archive, taille, empreinte, tables de `Gr.fst`)

- **Produit par :** `dev`, mission 016, mesuré le **2026-10-24** (téléchargement à 12:27 UTC).
- **Décision éclairée :** la marge d'espace de la mise à jour de `terrain-voix` (mission 018) et
  l'empreinte à figer pour les applications (ADR 0005, `docs/adr/0005-installation-modele-vosk.md`,
  sur la branche `pilotage/brief-voix` tant qu'elle n'est pas sur `main`).
- **Statut :** mesures. **Aucune marge n'est proposée ici** (c'est la mission 018).
- **Ce qui est mesuré et ce qui ne l'est pas :** tout chiffre ci-dessous vient d'une commande
  rejouable (§7). Le modèle n'a pas été chargé dans Vosk : rien n'est dit de son fonctionnement.

## 1. L'archive

- **URL consultée :** https://alphacephei.com/vosk/models/vosk-model-small-fr-0.22.zip
  (HTTP 200, sans redirection ; en-têtes lus par `curl -sSIL`).
- **Version :** `0.22`, dans le nom. L'en-tête HTTP `Last-Modified` est `Tue, 25 Jan 2022 21:31:23 GMT`
  et `ETag` `"61f06c2b-2846deb"` ; les dates des entrées du zip vont du 2020-12-24 au 2022-01-25.
- **Taille du fichier :** **42 233 323 octets** (`stat -c %s`, égale au `Content-Length` et à
  `curl -w %{size_download}`). La page https://alphacephei.com/vosk/models dit « 41M » pour ce modèle
  (arrondi : 42 233 323 / 1 048 576 = 40,28 Mio ; 42,2 Mo en décimal).
- **Empreinte SHA-256 de l'archive :** `sha256sum m.zip` →

  ```
  cabf6180e177eb9b3a9a9d43a437bd5e549f3a7d09525e5d69a3fed787be12ad
  ```

  Calculée par nous le 2026-10-24 : la page des modèles ne publie aucune empreinte (`grep -i -c -E
  "sha|md5|checksum"` sur la page téléchargée : 0), comme le dit l'ADR 0005. Elle vaut pour le
  fichier servi ce jour-là ; si alphacephei le refait, elle change (`ETag` et `Last-Modified`
  ci-dessus aident à le voir).
- **Intégrité :** `unzip -tq m.zip` → « No errors detected in compressed data of m.zip ».

## 2. Tailles

| Grandeur | Valeur | Commande |
|---|---|---|
| Archive (fichier `.zip`) | 42 233 323 octets | `stat -c %s m.zip` |
| Données compressées (somme des entrées) | 42 229 183 octets | `zipinfo -t m.zip` (reste du fichier : 4 140 octets d'en-têtes et de répertoire central) |
| **Décompressé (somme des tailles des fichiers)** | **68 483 807 octets** (65,31 Mio ; 68,5 Mo) | `unzip -l m.zip` (total) ; `find . -type f -printf '%s\n' \| awk '{s+=$1}END{print s, NR}'` sur le dossier extrait : 68483807 et 14 fichiers |
| Décompressé, occupation disque (blocs) | 66 944 Kio = 68 550 656 octets | `du -sk` sur le dossier extrait (blocs de 4 Kio de la machine de mesure ; dépend du système de fichiers) |
| Taux | 38,3 % | `zipinfo -t` |

Pour la mise à jour (l'ADR 0005 parle d'« environ trois fois la taille du modèle », « non mesuré ») :
3 × 68 483 807 = 205 451 421 octets (195,9 Mio) ; 3 × 42 233 323 = 126 699 969 octets. Ce n'est que de
l'arithmétique : **le sens exact de « trois fois » n'est pas mesuré ici** et la marge relève de la
mission 018. À titre de repère, l'ancien modèle décompressé (68 483 807), le nouveau décompressé
(68 483 807) et l'archive (42 233 323) cohabitent pour 179 200 937 octets (170,9 Mio) tant que l'archive
n'est pas supprimée.

## 3. Liste des fichiers (20 entrées : 14 fichiers et 6 dossiers)

Racine de l'archive : `vosk-model-small-fr-0.22/`, avec son entrée de dossier (donc un dossier de tête,
pas de `__MACOSX/`). Les 6 dossiers sont des entrées explicites de taille 0 : racine, `graph/`,
`graph/phones/`, `am/`, `conf/`, `ivector/`. Commande : `unzip -l m.zip`. SHA-256 de chaque fichier extrait :
`find . -type f | sort | xargs sha256sum`.

| Fichier (sous la racine) | Taille (octets) | SHA-256 |
|---|---:|---|
| `README` | 354 | `a036c27a5f30d95910f739ce9466539a5772cd13541e5fdddfbfae187993f8d8` |
| `am/final.mdl` | 15 978 979 | `580d99752e3f6f31fb4a03403b0b2a3a78b9665937e2823668b46d9c9ce1a1a1` |
| `conf/mfcc.conf` | 153 | `6c326fea5741c2d78be12f76eb1b36ea2ace040da9e355e4cfe6bb80e5f7d5ab` |
| `conf/model.conf` | 289 | `d8d91d310a9659fed2d89f8ea4996af6163a43c3ab008cd278e4264120551712` |
| `graph/Gr.fst` | 24 029 163 | `4d5aefc6af664d1c47979ae3b9eb7e972c196e843ff4eee65e4c7b0c064723cf` |
| `graph/HCLr.fst` | 18 331 442 | `6562aee2cffac5ad2e569584bc8ca22b1bae0ce9e6e03f0f9898d9d7c267eaa5` |
| `graph/disambig_tid.int` | 380 | `fd844580478d2c0dbb01e408f77fce92a343a9fe05c304fea57a0d8e3122d7b1` |
| `graph/phones/word_boundary.int` | 1 626 | `262452069835201f18fd4de64c4816a2333a54c80b4114084d75e1c4eb273836` |
| `ivector/final.dubm` | 168 048 | `15c6736f097e9a1fbc3811a75a666da39cb9aa4526acea29d25f2a3003357acc` |
| `ivector/final.ie` | 9 927 287 | `bd52bc5aeef1f35349c7a6fd3fb671a4a877ba04181da30d99f2cf01d4b75dd5` |
| `ivector/final.mat` | 44 975 | `61818a44ae3519708e9a31701da4ab351f3e19d7bd3e4ddfd820fd7f121cfad0` |
| `ivector/global_cmvn.stats` | 1 076 | `9d94d076adc26cd578cc4cf702dc41e5ea6de202f4563d6f0d4b4d4fdbb46bc1` |
| `ivector/online_cmvn.conf` | **0** | `e3b0c44298fc1c149afbf4c8996fb92427ae41e4649b934ca495991b7852b855` (SHA-256 d'un contenu vide) |
| `ivector/splice.conf` | 35 | `9f0c5f7c82d18eaf25d8bce470efa9f7741f88411fe428774bc0a9bb69a24756` |

Total : 68 483 807 octets, 14 fichiers.

### Présence des fichiers que `terrain-voix` attend (ADR 0005)

| Fichier | Présent ? |
|---|---|
| `am/final.mdl` | **oui** (15 978 979 octets) |
| `conf/model.conf` | **oui** (289 octets) |
| `graph/HCLr.fst` | **oui** (18 331 442 octets) |
| `graph/Gr.fst` | **oui** (24 029 163 octets) |
| `graph/HCLG.fst` | **non** (le modèle n'est donc pas refusé pour `SansGrammaireDynamique`) |
| `ivector/` | **oui** (dossier ; 6 fichiers, dont `online_cmvn.conf` **vide**) |
| `graph/words.txt` | **non : absent de l'archive** |

Deux faits à ne pas manquer pour les missions suivantes :

1. **`graph/words.txt` n'existe pas dans cette archive.** Un contrôle de structure qui l'exigerait
   refuserait le modèle. Le vocabulaire est dans les tables de symboles de `Gr.fst` (§4). Dans
   `src/model.cc` de vosk-api (`master`, commit `4ab9821510b488efb7740094714a130bd3545e90`, lu le
   2026-10-24), `words.txt` n'est lu (l. 309-315) que si ni `HCLG.fst` ni `Gr.fst` ne porte de table de
   symboles de sortie (l. 304-308) ; ici `Gr.fst` en porte une.
2. **`ivector/online_cmvn.conf` est un fichier vide** (0 octet, entrée de fichier et non de dossier).
   Un contrôle « aucun fichier vide » le refuserait à tort. `model.cc` (l. 263) le nomme dans la
   configuration de l'extracteur d'i-vecteurs ; la mesure ne dit pas si Vosk en tolère le vide
   (modèle non chargé).

Contenu des fichiers de configuration (`cat`) : `conf/model.conf` fixe `--min-active=200`,
`--max-active=7000`, `--beam=13.0`, `--lattice-beam=4.0`, `--acoustic-scale=1.0`,
`--frame-subsampling-factor=3` et les règles de fin de parole `--endpoint.*`. Le `README` du modèle
donne des WER de 23,95 % (cv), 19,30 % (mtedx) et 27,25 % (podcast), repris tels quels, non revérifiés.

## 4. Les tables de symboles d'entrée et de sortie de `graph/Gr.fst`

**Réponse : identiques, octet pour octet.** Chaque table compte 135 774 symboles, porte le même nom
(`exp/chain_a/tdnn/lgraph/words.txt`) et la même clé libre (135 774) ; les paires (symbole, clé) sont les
mêmes, dans le même ordre ; et les 2 826 405 octets de chacune sont égaux (table d'entrée : octets 65 à
2 826 470 exclu ; table de sortie : 2 826 470 à 5 652 875 exclu ; fichier de 24 029 163 octets).

**Comment cela a été fait, et ce que l'outil tiers n'a pas pu faire :**

- Outil tiers visé : `fstsymbols`/`fstprint` d'OpenFst. Ils ne sont pas installés sur la machine
  (`command -v fstprint fstsymbols` : rien). De plus `Gr.fst` est de type `ngram` (arcs `standard`,
  version 4) : OpenFst ne le lit pas sans l'extension `ngram-fst.so` (erreur « Unknown FST type
  ngram »), absente du paquet Python utilisé (`pynini` 2.1.7, son `pywrapfst`, installé dans un
  environnement virtuel hors du dépôt).
- **Méthode retenue (script hors du dépôt, dit tel quel, §7) :** il lit l'en-tête OpenFst
  (petit-boutiste), constate les indicateurs = 3 (table d'entrée et table de sortie présentes), parcourt
  les deux tables à la suite et les compare ; puis il **découpe chaque table dans un fichier que
  `SymbolTable.read` d'OpenFst (via `pywrapfst`) relit**. Les deux lectures donnent 135 774 symboles et la
  même somme de contrôle d'étiquettes (`labeled_checksum`, égale pour les deux). Le découpage et la
  comparaison viennent de notre script ; la lecture des tables et la somme de contrôle viennent d'OpenFst.
- Limites : le graphe lui-même n'a pas été chargé ; l'en-tête donne 247 373 états et 0 arc (valeur du
  type `ngram`), non exploités.

Conséquence pour `VocabulaireVosk` : lire la seule table d'entrée (indicateur `0x1`, comme le fait
`motsConnus`) donne les mêmes mots que la table de sortie. Cela n'est vrai que pour cette archive.
Le commentaire de `VocabulaireVosk.kt` dit « 135 000 mots » ; la mesure est **135 774** symboles. Lus sur
l'hexadécimal de `Gr.fst` (`xxd -s 0x70 -l 100`), `<eps>` a la clé 0 et `[unk]` figure dans la table
(clé 135 769, lue à la main sur l'octet).

## 5. Sources consultées (toutes le 2026-10-24)

| Source | Usage |
|---|---|
| https://alphacephei.com/vosk/models/vosk-model-small-fr-0.22.zip | l'archive (curl) |
| https://alphacephei.com/vosk/models | taille « 41M » ; absence d'empreinte (curl, page HTML) |
| https://raw.githubusercontent.com/alphacep/vosk-api/master/src/model.cc (commit `4ab9821`, `master` le 2026-10-24, relevé par `https://api.github.com/repos/alphacep/vosk-api/commits/master`) | l. 195-216 : disposition `am/`, `conf/`, `graph/`, `ivector/` ; l. 263 : `online_cmvn.conf` ; l. 291-315 : `HCLG.fst` sinon `HCLr.fst` + `Gr.fst`, `words.txt` en dernier recours |
| https://raw.githubusercontent.com/alphacep/vosk-api/master/src/recognizer.cc (même commit) | l. 58-61 et 284-285 : « Runtime graphs are not supported by this model » si `hcl_fst_` est absent |
| `terrain-voix/src/main/kotlin/fr/nemeton/terrain/voix/VocabulaireVosk.kt` (branche `pilotage/brief-voix`) | format de l'en-tête OpenFst, indicateurs |

Les numéros de ligne de `model.cc` et `recognizer.cc` valent pour le commit `4ab9821` ; ils changent
avec la version.

## 6. Ce qui n'est pas mesuré

- Le modèle n'a pas été chargé dans Vosk (ni reconnaissance, ni grammaire dynamique essayée).
- L'espace libre d'un téléphone de terrain : c'est l'échantillon de Pascal, ci-dessous.
- Le « trois fois » de l'ADR 0005 : arithmétique seule, au §2.
- Les autres versions ou miroirs de l'archive.
- La stabilité de l'archive dans le temps (une seule observation).

**Échantillon pour Pascal :** 68 483 807 octets décompressés (≈ 69 Mo) sont-ils compatibles, au triple,
avec l'espace libre d'un téléphone de terrain pendant une mise à jour ?

## 7. Rejouer

```sh
cd "$(mktemp -d)"                                   # hors du dépôt
curl -sSIL https://alphacephei.com/vosk/models/vosk-model-small-fr-0.22.zip | grep -i -E '^(HTTP|content-length|last-modified|etag)'
curl -sSL -o m.zip https://alphacephei.com/vosk/models/vosk-model-small-fr-0.22.zip
stat -c %s m.zip; sha256sum m.zip; unzip -tq m.zip
unzip -l m.zip; zipinfo -t m.zip
mkdir ex && unzip -q m.zip -d ex && cd ex/vosk-model-small-fr-0.22
find . -type f -printf '%s\n' | awk '{s+=$1}END{print s, NR}'; find . -type f | sort | xargs sha256sum; du -sk .
# tables de Gr.fst (cmp.py ci-dessous, rangé dans un autre dossier) :
# python3 -m venv v && v/bin/pip install pynini && v/bin/python -I cmp.py graph/Gr.fst
```

Script `cmp.py` (hors du dépôt, Python 3.12, `pynini` 2.1.7) :

```python
import sys, struct, os, tempfile, pywrapfst as f
p = sys.argv[1]
b = open(p, "rb").read()
o = 0
def i32():
    global o; v = struct.unpack_from("<i", b, o)[0]; o += 4; return v
def i64():
    global o; v = struct.unpack_from("<q", b, o)[0]; o += 8; return v
def s():
    global o; n = i32(); v = b[o:o+n].decode("utf-8"); o += n; return v
print("magic", hex(i32() & 0xffffffff), "fsttype", s(), "arctype", s(), "version", i32())
flags = i32(); print("flags", flags, "(1=isymbols, 2=osymbols)")
print("props", hex(i64()), "start", i64(), "states", i64(), "arcs", i64())
tables = []
for k in (1, 2):
    if not flags & k: tables.append(None); continue
    st = o
    assert i32() & 0xffffffff == 0x7eb2fb74
    name = s(); avail = i64(); n = i64()
    pairs = []
    for _ in range(n):
        sym = s(); key = i64(); pairs.append((sym, key))
    tables.append((name, avail, n, pairs, st, o))
    print("table", k, "name", name, "available_key", avail, "size", n, "bytes", st, "->", o)
print("end of the two tables:", o, "file size:", len(b))
a, c = tables
print("tables identical (name, available_key, size, pairs in order):", a[:4] == c[:4])
print("pairs identical as sets:", set(a[3]) == set(c[3]))
print("raw bytes identical:", b[a[4]:a[5]] == b[c[4]:c[5]])
d = tempfile.mkdtemp()
for k, t in enumerate(tables):
    q = os.path.join(d, "t%d" % k); open(q, "wb").write(b[t[4]:t[5]])
    st = f.SymbolTable.read(q)
    print("OpenFst SymbolTable.read table", k+1, "num_symbols", st.num_symbols(), "checksum", st.labeled_checksum())
```

Sortie relevée : « tables identical (name, available_key, size, pairs in order): True », « pairs
identical as sets: True », « raw bytes identical: True », puis `num_symbols 135774` et le même
`checksum` pour les tables 1 et 2.
