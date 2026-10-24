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
fichier $R/graph/disambig_tid.int disambig
fichier $R/nouveau.txt nouveau
fichier $R/a-b.txt interne
fichier "__MACOSX/$R/._final.mdl" mac
fichier $R/.DS_Store ds
fichier evade.txt evade
# Fichier de 0 octet, comme ivector/online_cmvn.conf dans le vrai modèle.
mkdir -p "$T/$R/ivector"
: > "$T/$R/ivector/online_cmvn.conf"
touch -d "$DATE" "$T/$R/ivector/online_cmvn.conf"
# Sans dossier de tête : mêmes fichiers à la racine du zip.
fichier am/final.mdl final
fichier conf/model.conf conf
fichier graph/HCLr.fst hclr
fichier graph/Gr.fst gr
fichier graph/disambig_tid.int disambig
fichier nouveau.txt nouveau

# La date du dossier racine compte aussi (entrée « vosk-model-test/ » de racine-seule.zip).
touch -d "$DATE" "$T/$R"

# Les cinq fichiers obligatoires d'un modèle valide.
BON="$R/am/final.mdl $R/conf/model.conf $R/graph/HCLr.fst $R/graph/Gr.fst $R/graph/disambig_tid.int"

# 1. bonne (avec racine) : 6 fichiers, 6+5+5+3+9+8 = 36 octets décompressés
zipper bonne.zip $BON $R/nouveau.txt
# 2. bonne sans racine
zipper bonne-sans-racine.zip am/final.mdl conf/model.conf graph/HCLr.fst graph/Gr.fst graph/disambig_tid.int nouveau.txt
# 3. tronquée : la bonne, coupée aux deux tiers (plus de répertoire central)
taille=$(wc -c < bonne.zip)
head -c $((taille * 2 / 3)) bonne.zip > tronquee.zip
# 4. vide (0 octet)
: > vide.zip
# 5. zip sans entrée : seul l'enregistrement de fin de répertoire central (22 octets)
printf 'PK\005\006\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000' > sans-entree.zip
# 6. racine seule : exactement l'entrée du dossier racine « vosk-model-test/ », rien d'autre
#    (sans -r, zip n'ajoute que l'entrée du dossier)
zipper racine-seule.zip $R
# 7. sans am/final.mdl (le reste est complet)
zipper sans-final-mdl.zip $R/conf/model.conf $R/graph/HCLr.fst $R/graph/Gr.fst $R/graph/disambig_tid.int $R/nouveau.txt
# 8. à HCLG.fst, sans HCLr.fst (le reste est complet)
zipper hclg.zip $R/am/final.mdl $R/conf/model.conf $R/graph/HCLG.fst $R/graph/Gr.fst $R/graph/disambig_tid.int $R/nouveau.txt
# 9. avec __MACOSX/ et .DS_Store
zipper avec-macosx.zip $BON $R/nouveau.txt "__MACOSX/$R/._final.mdl" $R/.DS_Store
# 10. avec une entrée qui sort du dossier : « vosk-model-test/../../evade-017.txt »
zipper evasion.zip $BON evade.txt
printf '@ evade.txt\n@=%s/../../evade-017.txt\n' $R | zipnote -w evasion.zip
# 11. sans graph/disambig_tid.int (obligatoire : sans lui, Vosk s'arrête en grammaire dynamique)
zipper sans-disambig.zip $R/am/final.mdl $R/conf/model.conf $R/graph/HCLr.fst $R/graph/Gr.fst $R/nouveau.txt
# 12. bonne, avec un fichier vide (0 octet) : ivector/online_cmvn.conf
zipper avec-fichier-vide.zip $BON $R/ivector/online_cmvn.conf $R/nouveau.txt
# 13. entrée « vosk-model-test/a/../b » : reste sous la racine, mais son chemin contient « .. »
zipper point-point-interne.zip $BON $R/a-b.txt
printf '@ %s/a-b.txt\n@=%s/a/../b\n' $R $R | zipnote -w point-point-interne.zip

rm -rf "$T"
# Empreintes de référence : calculées par sha256sum, jamais par le code testé.
sha256sum bonne.zip bonne-sans-racine.zip tronquee.zip vide.zip sans-entree.zip racine-seule.zip \
  sans-final-mdl.zip hclg.zip avec-macosx.zip evasion.zip sans-disambig.zip avec-fichier-vide.zip \
  point-point-interne.zip > empreintes.txt
