#!/usr/bin/env python3
"""Fabrique reference.csv : rapports de contraste WCAG 2.x calcules par DEUX outils tiers.

Format : separateur VIRGULE, point decimal (a l'import dans un tableur regle en francais,
choisir « virgule » comme separateur et « point » comme separateur decimal).

Aucun code de terrain-style n'intervient. Chaque couple est calcule par (voir calcul.mjs) :
  - color2k 2.0.4 (npm, Rico Kahler)  : getContrast
  - culori 4.0.2  (npm, Dan Burzo)    : wcagContrast
Les deux appliquent la formule WCAG a la lettre (0,2126 / 0,7152 / 0,0722, seuil 0,04045).
Le rapport est TRONQUE a 3 decimales (jamais arrondi, WCAG). Un couple pour lequel les deux
outils donnent des valeurs tronquees differentes est ecarte (et signale sur stderr), sans arbitrage.

Usage (Node >= 18), depuis ce dossier :
  mkdir /tmp/w && cd /tmp/w && npm init -y && npm i color2k@2.0.4 culori@4.0.2 && cp <ici>/calcul.mjs .
  CALCUL_DIR=/tmp/w python3 fabriquer.py > reference.csv
"""
import json
import math
import os
import random
import subprocess
import sys

DIR = os.environ.get("CALCUL_DIR", os.path.dirname(os.path.abspath(__file__)))


def tronque(x):
    # troncature a 3 decimales ; 1e-6 absorbe le bruit flottant (x.xxx999999...)
    return math.floor(x * 1000 + 1e-6) / 1000


fixes = [
    (0xFFFFFFFF, 0xFF000000),  # noir/blanc = 21
    (0xFFFFFFFF, 0xFFFFFFFF),  # identiques = 1
    (0xFF3E7A40, 0xFF3E7A40),
    (0xFFFFFFFF, 0xFF3E7A40),  # cas de la spec : 5,17
    (0xFF3E7A40, 0xFFFFFFFF),  # ordre inverse
    (0xFF374742, 0xFFFFFFFF),  # VertMarculus sur blanc
    (0xFF374742, 0xFF000000),
    (0xFFFFFF00, 0xFF000000),  # jaune pur / noir
    (0xFFFF0000, 0xFF000000),
    (0xFF000000, 0xFF000001),  # quasi-noirs
    (0xFF0A0A0A, 0xFF0B0B0B),  # de part et d'autre du seuil 0,04045 (canal 10/11)
    (0xFF767676, 0xFFFFFFFF),  # gris limite 4,5:1
    (0xFF777777, 0xFFFFFFFF),  # juste sous 4,5 (seuil sur le rapport brut)
    (0xFF595959, 0xFFFFFFFF),  # 7,0 environ
    (0xFF808080, 0xFF7F7F7F),
]
rnd = random.Random(2154)
pairs = list(fixes)
for _ in range(185):
    pairs.append((0xFF000000 | rnd.getrandbits(24), 0xFF000000 | rnd.getrandbits(24)))

res = subprocess.run(
    ["node", os.path.join(DIR, "calcul.mjs")],
    input=json.dumps(pairs), capture_output=True, text=True, check=True, cwd=DIR,
)
data = json.loads(res.stdout)
v = data["versions"]

print("argb_a,argb_b,rapport,outil,version")
for (a, b), (r1, r2) in zip(pairs, data["lignes"]):
    t1, t2 = tronque(r1), tronque(r2)
    if abs(t1 - t2) > 1e-9:
        print(f"Ecarte (outils en desaccord apres troncature) : {a:#010X} {b:#010X} {t1} / {t2}", file=sys.stderr)
        continue
    print(f"0x{a:08X},0x{b:08X},{t1:.3f},color2k,{v['color2k']}")
    print(f"0x{a:08X},0x{b:08X},{t2:.3f},culori,{v['culori']}")
