// Calcule le rapport de contraste WCAG 2.x de couples ARGB par DEUX outils tiers.
// Entrée (stdin) : JSON [[argbA, argbB], ...]. Sortie : JSON {versions, lignes:[[r_color2k, r_culori], ...]}.
// Les deux outils appliquent la formule WCAG a la lettre (0,2126 / 0,7152 / 0,0722, seuil 0,04045),
// sont d'auteurs distincts et entretenus :
//   - color2k 2.0.4  (Rico Kahler, MIT)   https://github.com/ricokahler/color2k
//   - culori 4.0.2   (Dan Burzo, MIT)     https://github.com/Evercoder/culori
import { readFileSync } from 'node:fs';
import { createRequire } from 'node:module';
import { getContrast } from 'color2k';
import { wcagContrast } from 'culori';

const require = createRequire(import.meta.url);
const hex = (n) => '#' + (n & 0xffffff).toString(16).padStart(6, '0');
const pairs = JSON.parse(readFileSync(0, 'utf8'));
const out = pairs.map(([a, b]) => [getContrast(hex(a), hex(b)), wcagContrast(hex(a), hex(b))]);
const v = (p) => require(`${p}/package.json`).version;
console.log(JSON.stringify({ versions: { color2k: v('color2k'), culori: v('culori') }, lignes: out }));
