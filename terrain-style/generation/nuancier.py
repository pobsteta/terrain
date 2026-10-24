#!/usr/bin/env python3
"""Dessine le nuancier (nuancier.svg) du style de Marculus : les 48 rôles et reussi/alerte,
en clair et en sombre, avec le nom et l'hexa de chacun.

Entrées : le CSV du référentiel (`src/test/resources/jetons/style-marculus.csv`) et
`StyleTerrain.kt` (pour reussi et alerte). Sortie : `generation/nuancier.svg`.
Usage : python3 -I terrain-style/generation/nuancier.py   (depuis la racine du dépôt)
La CI rejoue ce script et échoue sur toute différence.
"""
import csv
import re
import sys
from pathlib import Path

module = Path(__file__).resolve().parent.parent
LARG, HAUT, MARGE = 300, 24, 10

THEMES = ("clair", "sombre")


def lire_roles():
    roles = {t: [] for t in THEMES}
    with open(module / "src/test/resources/jetons/style-marculus.csv", newline="") as f:
        for r in csv.DictReader(f):
            roles[r["theme"]].append((r["role"], int(r["argb"], 16) & 0xFFFFFF))
    return roles


def lire_semantiques():
    src = (module / "src/main/kotlin/fr/nemeton/terrain/style/StyleTerrain.kt").read_text()
    clair, sombre = src.split("val sombre")
    res = {}
    for theme, texte in (("clair", clair), ("sombre", sombre)):
        res[theme] = []
        for nom in ("reussi", "alerte"):
            m = re.search(nom + r" = Semantique\(([^)]*)\)", texte)
            vals = [int(v.strip()[2:-1], 16) & 0xFFFFFF for v in m.group(1).split(",")]
            for champ, v in zip(("couleur", "surCouleur", "conteneur", "surConteneur"), vals):
                res[theme].append((f"{nom}.{champ}", v))
    return res


def luminance(v):
    def lin(c):
        c /= 255
        return c / 12.92 if c <= 0.04045 else ((c + 0.055) / 1.055) ** 2.4
    return 0.2126 * lin(v >> 16 & 255) + 0.7152 * lin(v >> 8 & 255) + 0.0722 * lin(v & 255)


def encre(v):
    l = luminance(v)
    noir = (l + 0.05) / 0.05
    blanc = 1.05 / (l + 0.05)
    return "#000000" if noir >= blanc else "#FFFFFF"


def rgb(v):
    return "#%06X" % v


def main():
    roles, sem = lire_roles(), lire_semantiques()
    n = max(len(roles[t]) + len(sem[t]) for t in THEMES)
    entete = 3 * HAUT
    l = 2 * LARG + 3 * MARGE
    h = entete + n * HAUT + 2 * MARGE
    vert_vif = dict(roles["clair"])["primary"]
    vert_marculus = dict(roles["clair"])["primaryContainer"]
    out = [
        f'<svg xmlns="http://www.w3.org/2000/svg" width="{l}" height="{h}" viewBox="0 0 {l} {h}" font-family="sans-serif" font-size="12">',
        f'  <rect width="{l}" height="{h}" fill="#FFFFFF"/>',
        f'  <rect x="{MARGE}" y="{MARGE}" width="{LARG}" height="{HAUT}" fill="{rgb(vert_vif)}"/>',
        f'  <text x="{MARGE + 6}" y="{MARGE + 16}" fill="{encre(vert_vif)}">VertVif {rgb(vert_vif)} (primary)</text>',
        f'  <rect x="{2 * MARGE + LARG}" y="{MARGE}" width="{LARG}" height="{HAUT}" fill="{rgb(vert_marculus)}"/>',
        f'  <text x="{2 * MARGE + LARG + 6}" y="{MARGE + 16}" fill="{encre(vert_marculus)}">VertMarculus {rgb(vert_marculus)} (primaryContainer)</text>',
    ]
    for i, t in enumerate(THEMES):
        x = MARGE + i * (LARG + MARGE)
        out.append(f'  <text x="{x}" y="{MARGE + HAUT + 18}" fill="#000000" font-weight="bold">{t}</text>')
        for k, (nom, v) in enumerate(roles[t] + sem[t]):
            y = MARGE + entete + k * HAUT
            note = " (interdit en texte)" if nom == "alerte.couleur" else ""
            out.append(f'  <rect x="{x}" y="{y}" width="{LARG}" height="{HAUT}" fill="{rgb(v)}"/>')
            out.append(f'  <text x="{x + 6}" y="{y + 16}" fill="{encre(v)}">{nom} {rgb(v)}{note}</text>')
    out.append("</svg>")
    (module / "generation/nuancier.svg").write_text("\n".join(out) + "\n")


if __name__ == "__main__":
    sys.exit(main())
