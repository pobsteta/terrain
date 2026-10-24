#!/bin/sh
# Fabrique les archives d'essai de l'installation du modèle Vosk, et leurs empreintes
# (empreintes.txt) avec Info-ZIP (zip, zipnote) et sha256sum (GNU coreutils).
# Les fichiers de modèle sont factices (quelques octets). Les archives sont « stockées » (-0),
# sans attributs additionnels (-X), à date fixée, en TZ=UTC et dans un ordre fixe : rejouer
# ce script redonne les mêmes octets (contrôle : sha256sum -c empreintes.txt).
# Usage : ./fabriquer.sh   (à relancer seulement pour changer le jeu)
set -e
cd "$(dirname "$0")"
export TZ=UTC
T=$(mktemp -d)
R=vosk-model-test
DATE='2026-01-01 00:00:00'

# fichier <chemin> <contenu> : crée le fichier sous $T avec une date fixe
fichier() {
  mkdir -p "$T/$(dirname "$1")"
  printf '%s\n' "$2" > "$T/$1"
  touch -d "$DATE" "$T/$1"
}
# zipper <archive> <chemin>... : zippe dans cet ordre, depuis $T
zipper() {
  out="$PWD/$1"; shift
  rm -f "$out"
  (cd "$T" && printf '%s\n' "$@" | zip -X -0 -q "$out" -@)
}

fichier $R/am/final.mdl final
fichier $R/conf/model.conf conf
fichier $R/graph/HCLr.fst hclr
fichier $R/graph/Gr.fst gr
fichier $R/graph/HCLG.fst hclg
fichier $R/nouveau.txt nouveau
fichier "__MACOSX/$R/._final.mdl" mac
fichier $R/.DS_Store ds
fichier evade.txt evade
mkdir -p "$T/$R/vide"; touch -d "$DATE" "$T/$R/vide"
# Sans dossier de tête : mêmes fichiers à la racine du zip.
fichier am/final.mdl final
fichier conf/model.conf conf
fichier graph/HCLr.fst hclr
fichier graph/Gr.fst gr
fichier nouveau.txt nouveau

# 1. bonne (avec racine) : 5 fichiers, 6+5+5+3+8 = 27 octets décompressés
zipper bonne.zip $R/am/final.mdl $R/conf/model.conf $R/graph/HCLr.fst $R/graph/Gr.fst $R/nouveau.txt
# 2. bonne sans racine
zipper bonne-sans-racine.zip am/final.mdl conf/model.conf graph/HCLr.fst graph/Gr.fst nouveau.txt
# 3. tronquée : la bonne, coupée aux deux tiers (plus de répertoire central)
taille=$(wc -c < bonne.zip)
head -c $((taille * 2 / 3)) bonne.zip > tronquee.zip
# 4. vide (0 octet)
: > vide.zip
# 5. zip sans entrée : seul l'enregistrement de fin de répertoire central (22 octets)
printf 'PK\005\006\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000' > sans-entree.zip
# 6. racine seule : une entrée de dossier, rien dedans
zipper racine-seule.zip $R/vide
# 7. sans am/final.mdl
zipper sans-final-mdl.zip $R/conf/model.conf $R/graph/HCLr.fst $R/graph/Gr.fst $R/nouveau.txt
# 8. à HCLG.fst, sans HCLr.fst
zipper hclg.zip $R/am/final.mdl $R/conf/model.conf $R/graph/HCLG.fst $R/graph/Gr.fst $R/nouveau.txt
# 9. avec __MACOSX/ et .DS_Store
zipper avec-macosx.zip $R/am/final.mdl $R/conf/model.conf $R/graph/HCLr.fst $R/graph/Gr.fst $R/nouveau.txt "__MACOSX/$R/._final.mdl" $R/.DS_Store
# 10. avec une entrée qui sort du dossier : « vosk-model-test/../../evade-017.txt »
zipper evasion.zip $R/am/final.mdl $R/conf/model.conf $R/graph/HCLr.fst $R/graph/Gr.fst evade.txt
printf '@ evade.txt\n@=%s/../../evade-017.txt\n' $R | zipnote -w evasion.zip

rm -rf "$T"
# Empreintes de référence : calculées par sha256sum, jamais par le code testé.
sha256sum bonne.zip bonne-sans-racine.zip tronquee.zip vide.zip sans-entree.zip racine-seule.zip \
  sans-final-mdl.zip hclg.zip avec-macosx.zip evasion.zip > empreintes.txt
