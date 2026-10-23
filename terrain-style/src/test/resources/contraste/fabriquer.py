#!/usr/bin/env python3
"""Fabrique reference.csv : rapports de contraste WCAG 2.2 calcules par DEUX outils tiers.

Aucun code de terrain-style n'intervient. Chaque couple est calcule par :
  - wcag-contrast-ratio 0.9 (PyPI)
  - coloraide 4.0 (PyPI), methode « wcag21 »
Le rapport est TRONQUE a 3 decimales (jamais arrondi, WCAG). Un couple pour lequel les deux
outils donnent des valeurs tronquees differentes est ecarte (et signale sur stderr).

Usage :
  python3 -m venv /tmp/v && /tmp/v/bin/pip install wcag-contrast-ratio==0.9 coloraide==4.0
  /tmp/v/bin/python fabriquer.py > reference.csv
"""
import math
import random
import sys
from importlib.metadata import version

import wcag_contrast_ratio as wcr
from coloraide import Color


def rgb(argb):
    return ((argb >> 16) & 255) / 255, ((argb >> 8) & 255) / 255, (argb & 255) / 255


def outil_wcr(a, b):
    return wcr.rgb(rgb(a), rgb(b))


def outil_coloraide(a, b):
    ca, cb = Color("srgb", list(rgb(a))), Color("srgb", list(rgb(b)))
    return ca.contrast(cb, method="wcag21")


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
    (0xFF777777, 0xFFFFFFFF),  # juste sous 4,5 (troncature, pas arrondi)
    (0xFF595959, 0xFFFFFFFF),  # 7,0 environ
    (0xFF808080, 0xFF7F7F7F),
]
rnd = random.Random(2154)
pairs = list(fixes)
for _ in range(185):
    pairs.append((0xFF000000 | rnd.getrandbits(24), 0xFF000000 | rnd.getrandbits(24)))

print("argb_a,argb_b,rapport,outil,version")
for a, b in pairs:
    r1, r2 = tronque(outil_wcr(a, b)), tronque(outil_coloraide(a, b))
    if abs(r1 - r2) > 1e-9:
        # coloraide derive Y d'une matrice XYZ (ecart ~1e-4) : un couple dont la troncature
        # differe n'est pas une reference sure ; on l'ecarte et on le dit.
        print(f"Ecarte (outils en desaccord apres troncature) : {a:#010X} {b:#010X} {r1} / {r2}", file=sys.stderr)
        continue
    print(f"0x{a:08X},0x{b:08X},{r1:.3f},wcag-contrast-ratio,{version('wcag-contrast-ratio')}")
    print(f"0x{a:08X},0x{b:08X},{r2:.3f},coloraide,{version('coloraide')}")
