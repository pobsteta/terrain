# Mission 003 — tests-contraste

> Fiche de mission (fiches n° 9 et 15 de la méthode). Une mission = une phrase = une
> branche = une PR. Tirée de `docs/spec.md` (validée le 2026-10-23), issue #1.

## Mission

**En une phrase (quoi, et pourquoi) :** Écrire, sans lire le code qui l'implémente, les tests d'acceptation de `Contraste` et leur table de référence calculée par deux outils tiers, pour que la fonction de contraste ne soit pas son propre juge.

**Redite par l'agent :** _à compléter par l'agent avant d'agir — s'il n'y arrive pas, il s'arrête._

## Les trois questions (règle de staffing)

| Question | Réponse | Conséquence |
|---|---|---|
| Peut-il redire la mission en une phrase, avec son but ? | oui | — |
| Sait-il comment faire ? (sources, conventions nommées ci-dessous) | oui : WCAG 2.2 (luminance relative, seuil 0,04045, rapport, troncature), critères 3 et 5 de la spec, règle « outil tiers » de `CLAUDE.md` | — |
| A-t-il les moyens ? (outils, données d'essai, tests) | oui : Bash, Gradle, outils tiers installables (paquets Python ou npm nommés et versionnés) | — |

**Temps de la main :** appuyer.

## Ma réponse d'abord

_Rempli par Pascal seul, avant de lire le travail de l'agent : ce qu'il attend de la
mission, et le résultat qu'il prévoit. Une ligne suffit. L'orchestrateur ne le remplit
pas._

## L'Homme

- **Agent :** `testeur`
- **Modèle :** sonnet
- **Ce qui ne lui est pas demandé :** implémenter `Contraste` ; écrire les tests des jetons (mission 005).

## Harnais

- **Peut :** créer `terrain-style/src/test/` (tests Kotlin, `kotlin.test`), la table `argb_a,argb_b,rapport,outil,version` et le script qui la produit, ouvrir une PR.
- **Ne doit pas :** toucher à `main`, sortir du périmètre, inventer une valeur ou une source, trancher une question de valeur (la poser à Pascal) ; lire le code de `Contraste`
- **Sources autorisées :** `docs/spec.md` (API proposée, critères 3 et 5), WCAG 2.2 (W3C), `CLAUDE.md`, `terrain-gpkg/src/test/` comme modèle de jeu de référence.

## Tests

- **Comment saura-t-on que c'est juste :** les deux outils donnent la même valeur tronquée à 3 décimales sur chaque ligne ; `0xFFFFFFFF` / `0xFF3E7A40` = 5,17 ; cas limites : noir/blanc 21, couleur identique 1, ordre des arguments indifférent, extension de signe d'un `Int` négatif refusée.

## Terrain

- **Échantillon que Pascal vérifiera :** la table de référence, lisible dans un tableur.

## Qui veille, qui tranche

- **Relecteur :** `relecteur`, puis Pascal.
- **Critère d'arrêt de la relecture :** 3 allers-retours ; au-delà, la décision remonte à
  Pascal.
- **Point de suivi :** à la PR ; la mission 004 démarre dessus.
- **Qui tranche :** Pascal.

## Comment saura-t-on que ça marche ?

Les tests échouent (rouge) tant que la mission 004 n'est pas faite, puis passent.

## Décisions de Pascal et remarques du relecteur — aller-retour 1/3 (2026-10-23)

Relecteur : prêt (PR #3). Pascal a tranché, à reprendre sur la branche
`mission/003-tests-contraste` :

1. **Alpha ≠ `FF` : refusé.** Le test reste ; la spec le dit désormais (`docs/spec.md`,
   Données).
2. **Type d'exception : `IllegalArgumentException`.** Remplacer chaque `assertFails` par
   `assertFailsWith<IllegalArgumentException>` (convention de `terrain-gpkg` et
   `terrain-voix`).
3. **Outils tiers : remplacer `wcag-contrast-ratio` 0.9 (non entretenu depuis 2015) et
   `coloraide` 4.0 (luminance par matrice XYZ, écart jusqu'à 1,03e-3, pas ~1e-4) par
   deux outils qui appliquent la formule WCAG 2.x à la lettre (coefficients 0,2126 /
   0,7152 / 0,0722), entretenus, de deux auteurs distincts, versions épinglées et
   sourcées.** Régénérer la table ; les 21 couples écartés doivent pouvoir revenir. Si
   les deux outils divergent encore sur une ligne, l'écarter et le dire, sans arbitrer.
4. Renommer le test « 4,478 ne tient pas 4,5 et 4,542 le tient (pas d'arrondi…) » :
   il vérifie un seuil sur le rapport brut, pas une troncature.
5. En tête de `reference.csv` ou du script : rappeler « séparateur virgule, point
   décimal » pour l'import dans un tableur réglé en français.
6. Remplir « Redite par l'agent » en tête de fiche.
