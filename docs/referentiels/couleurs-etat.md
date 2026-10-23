# Référentiel — couleurs d'état (réussi, alerte) pour `terrain-style`

- **Propriétaire :** Pascal Obstétar
- **Produit par :** `acquereur`, mission 002, consulté le **2026-10-23**
- **Décision éclairée :** le choix par Pascal de la teinte de « réussi » et du jaune d'« alerte »
  (et de son noir), pour l'ADR 0002 (`docs/adr/0002-couleurs-semantiques.md`).
- **Statut :** matière à choisir. **Aucune valeur n'est choisie ici.**
- **Statut, ajouté le 2026-10-23 après l'ADR 0002 :** valeurs choisies (`reussi` provisoire jusqu'au 2026-10-30) ; ce fichier reste matière.
- **Limite de l'outil :** cette mission n'a eu aucun moyen d'exécuter du code. Toute conversion
  faite ci-dessous est **à la main** (méthode donnée) et marquée « à rejouer ». Aucun calcul
  HCT ni de contraste n'a été fait (hors mission, mission 007).

## 1. Ce qui est une référence et ce qui est une observation

| Donnée | Nature | Source unique retenue |
|---|---|---|
| Régions de chromaticité jaune/vert/noir de sécurité | référence (colorimétrie) | ISO 3864-4:2011, Table 1 |
| Valeur sRGB d'un jaune ou d'un vert « de sécurité » | observation (conversion, non normative) | aucune : il y en a plusieurs, voir §3 et §4 |

ISO 3864-4 ne donne **aucun hex** : elle donne des coordonnées (x, y) CIE 1931, observateur 2°,
illuminant D65, et un facteur de luminance β. Un hex est donc toujours une conversion.

## 2. Table 1 d'ISO 3864-4:2011 (lue, page 5 de l'aperçu iteh)

Titre : « Colour regions: chromaticity coordinates and luminance factor for object colours of
ordinary materials, phosphorescent materials and unpowered internally illuminated safety signs
under external illumination ». Illuminant CIE D65, observateur CIE 2°, géométrie 45°:0° ou 0°:45°
(§4.1). Source : https://cdn.standards.iteh.ai/samples/51000/096ba63848784a5f9bec95746e5b7196/ISO-3864-4-2011.pdf
(aperçu de 11 pages, lu en image).

```csv
couleur,point,x,y,beta_min,beta_max
jaune,1,0.475,0.525,0.45,0.70
jaune,2,0.538,0.462,0.45,0.70
jaune,3,0.470,0.424,0.45,0.70
jaune,4,0.427,0.472,0.45,0.70
vert,1,0.201,0.776,0.11,0.25
vert,2,0.285,0.441,0.11,0.25
vert,3,0.170,0.364,0.11,0.25
vert,4,0.026,0.399,0.11,0.25
noir,1,0.385,0.355,,0.03
noir,2,0.300,0.270,,0.03
noir,3,0.260,0.310,,0.03
noir,4,0.345,0.395,,0.03
blanc,1,0.350,0.360,0.75,
blanc,2,0.305,0.305,0.75,
blanc,3,0.285,0.315,0.75,
blanc,4,0.340,0.370,0.75,
```

Lecture de la table : la lecture du bloc « blanc » et « noir » est partiellement masquée par le
filigrane de l'aperçu ; les chiffres ci-dessus sont ceux lisibles, **à revérifier** sur le texte
payant avant tout usage. Les lignes jaune et vert sont nettes.

**Couleur de contraste du jaune :** ISO 3864-4 définit la « contrast colour » (3.3) et le contraste
`C = (βa − βb)/βa` (3.2) mais **la désignation « noir sur jaune » est dans ISO 3864-1**
(texte payant, non lu). Elle est confirmée par une source secondaire (Wikipédia, tableau
ISO 3864-2:2016 : Caution = jaune, contraste noir), voir §3.
Noir : β maximum **0,03** (Table 1), donc noir quasi absolu ; sRGB `#000000` (β = 0) est dans
la région, ce qui n'est pas une mesure mais une conséquence de β max.

## 3. Jaune de sécurité : sources comparées

| # | Source | Qui, pour quoi | Valeur | Conversion | Fiabilité |
|---|---|---|---|---|---|
| J1 | ISO 3864-4 Table 1 (primaire) | normalisation des matériaux de panneaux | région (x,y) + β 0,45 à 0,70 | aucune (pas d'hex) | forte, lue |
| J2 | Conversion à la main de J1 : moyenne des 4 sommets (x 0,4775 ; y 0,4708) | acquéreur, pour comparer | β 0,45 : lin. R 0,763 G 0,404 B −0,014 (B écrêté à 0) -> `#E2AA00` ; β 0,70 : R 1,19 G 0,63 B < 0 (hors gamut, écrêté) -> `#FFD000`. **L'écrêtage déplace la couleur** : `#FFD000` vaut ≈ β 0,66 et chromaticité ≈ (0,457 ; 0,475) (calcul du relecteur), encore dans la région ; ce n'est pas le β 0,70 visé | xyY -> XYZ -> sRGB linéaire (matrice IEC 61966-2-1, blanc D65, **sans adaptation** : D65 des deux côtés), encodage sRGB, écrêtage [0;1] | moyenne, **à rejouer par un outil**, pas un point normatif |
| J3 | RAL 1003 « Signal Yellow », rattaché à ISO 3864-4 (Wikipédia ISO 3864) | RAL, éditeur de collection | `#F3AD1E` (tableau « safety colours ») **et** `#F9A900` (tableau 3864-2, Wikipédia « ISO 3864 ») ; `#F9A800` (Wikipédia « List of RAL colours », relu 2026-10-23) | non précisée | secondaire, **trois valeurs entre elles** |
| J3b | Nuancier officiel RAL (ral-farben.de), RAL 1003 : https://www.ral-farben.de/en/colour/ral-classic/ral-1003/9086 | RAL gGmbH | `#E6B019` | **non précisée** : couleur d'affichage des pastilles du site, pas une mesure | éditeur de la collection (source la plus primaire pour RAL), mais valeur d'écran de méthode inconnue ; relue 2026-10-23 (relecteur, réponse 200) |
| J4 | ANSI Z535.1 « safety yellow » (LinkML valuesets) | ANSI, États-Unis (Munsell, illuminant C) | `#F6D04D`, PMS 116 C | non précisée, approximation tierce | secondaire, autre norme |

**Écart relevé :**
- J2 (`#E2AA00`, teinte HSL ≈ 45°) et J3 (`#F3AD1E` / `#F9A900`, ≈ 40°) concordent : un **jaune ambre**,
  pas un « jaune clair ». C'est le jaune ISO à son β minimum (0,45).
- J3b (`#E6B019`, nuancier officiel RAL, ≈ 44°) est de la même famille ambre que J2 et J3.
- J4 (`#F6D04D`, ≈ 46°) est nettement plus clair et plus pâle : c'est la norme **américaine**, pas ISO.
- Cause probable : ISO admet β de 0,45 à 0,70, donc une région large ; RAL 1003 en prend le bas,
  ANSI un jaune plus clair. Les teintes HSL (J2, J3, J4) sont voisines ; la différence est surtout de
  luminosité et de saturation.
- **Point de vigilance pour la spec :** le texte de la spec dit « jaune clair, texte noir ». Le jaune
  ISO sRGB le plus fidèle (J2/J3) est **ambre**, pas « clair » ; « jaune clair » ressemble à J4 (ANSI)
  ou à un β élevé de la région ISO, qui sort du gamut sRGB (rouge > 1, bleu < 0 : écrêté, donc
  approximation). Question posée à Pascal : « jaune clair » veut-il dire ISO à β haut, ANSI, ou
  autre chose ?
- Le jaune n'est pas affecté par `DislikeAnalyzer` ici (figé hors MCU, ADR 0002), mais ne pas
  l'affirmer sans mesure HCT (mission 007).

## 4. « Réussi » : sources comparées

| # | Source | Ce qu'elle dit | Valeur sRGB | Fiabilité |
|---|---|---|---|---|
| R1 | ISO 3864-4 Table 1, région verte | région très large (x 0,026 à 0,285 ; y 0,364 à 0,776), β 0,11 à 0,25 | non calculée (région trop large pour une teinte unique) | forte, lue ; **ne fixe pas une teinte** |
| R2 | RAL 6032 « Signal Green » rattaché à ISO 3864-4 (Wikipédia « ISO 3864 ») | vert « condition sûre » | `#31865C` (tableau 1) ; `#0F8558` (Wikipédia « List of RAL colours », relu 2026-10-23). `#237F52` **retiré** : aucune source retrouvée | secondaire, deux valeurs |
| R2b | Nuancier officiel RAL (ral-farben.de), RAL 6032 : https://www.ral-farben.de/en/colour/ral-classic/ral-6032/9218 | source la plus primaire pour RAL | `#417E57` | couleur d'affichage des pastilles du site, **méthode non précisée** par RAL, pas une mesure ; relue 2026-10-23 (relecteur, réponse 200) |
| R3 | ANSI Z535.1 « safety green » (LinkML) | vert « sécurité, premiers secours » | `#00843D`, PMS 355 C | secondaire |
| R4 | ISO 22324:2022 (alertes codées par couleur), via résumés : « Green is associated with a safe status … no action is required » ; jaune = « caution … prepare » ; rouge = danger | **sens** seulement, ne donne pas de teinte (hors champ : « the method for displaying colour codes ») | aucune | secondaire (l'aperçu ANSI webstore a répondu 403, non lu) |
| R5 | IEC 60073 | vert = condition normale/sûre, jaune = anormale (de mémoire, **non confirmé par un texte lu** ; seule la fiche de métadonnées a été trouvée) | aucune | non vérifiée |
| R6 | MDC-Android `docs/theming/Color.md` (lu) | ne traite **ni réussi, ni vert, ni jaune** ; seulement `error` ; harmonisation `harmonizeWithPrimary` (error harmonisé par défaut) ; pas de hex ni de ton HCT | aucune | forte, mais **ne répond pas** |

**Concordance :** les quatre valeurs R2, R2b et R3 s'étalent sur **142° à 157°** en HSL
(`#417E57` ≈ 142°, `#00843D` ≈ 148°, `#31865C` ≈ 150°, `#0F8558` ≈ 157° ; calcul à la main) : des verts
francs, légèrement bleutés, voisins mais pas identiques (15° d'étendue). `#237F52` est retiré (sans source). Teintes HSL, **pas HCT** : à ne pas
confondre. Ces valeurs sont des verts moyens ; la teinte de départ HCT reste à mesurer
(mission 007).

**Distance à `primary` (VertMarculus `#374742`) :** en HSL, teinte ≈ 161° pour `#374742`, soit
4 à 19° d'écart avec R2, R2b et R3 (4° pour `#0F8558`, R2 ; 19° pour `#417E57`, R2b), sur un vert très peu saturé (le vert d'état est beaucoup plus saturé).
La spec cite aussi `#3E7A40` (teinte HSL ≈ 122°, jaunâtre) comme couple d'essai de contraste, ce
n'est **pas** une source de teinte. **L'écart HCT avant/après harmonisation n'est pas calculé** (hors mission
et sans outil) : il reste dû à la mission 007. `Blend.harmonize` tourne d'au plus 15° (ADR 0002),
l'écart HSL de 4 à 19° (R2, R2b, R3) n'est **pas** une rotation HCT. **Hypothèse à mesurer** : harmoniser
pourrait rapprocher « réussi » de `primary` ; **c'est l'argument de fond de la question ouverte, à mesurer en HCT avant de trancher.**

**Retour hex → chromaticité (calcul du relecteur, aller-retour 1 ; approximatif : couleur d'écran assimilée à une couleur d'objet sous D65) :** `#F6D04D` (ANSI) est **hors** de la région jaune ISO ; `#31865C` et `#00843D` sont **hors** de la région verte ISO ; `#0F8558` est dedans. Non rejoué par l'acquéreur.

**Écart relevé :** pas de teinte « réussi » normative. Les normes donnent un **sens** (vert = sûr),
pas une couleur d'écran. Les hex existants sont des conversions tierces. Pascal choisit.

## 5. Ce qui n'a pas pu être vérifié (liste demandée par la fiche)

1. ISO 3864-1 (désignation noir sur jaune, vert/blanc de contraste) : payant, non lu.
2. ISO 3864-4, Annexe E (exemples de couleurs de sécurité et de contraste) : hors des 11 pages de
   l'aperçu iteh ; c'est elle qui donne peut-être des valeurs d'exemple.
3. ISO 22324:2022 : aperçu ANSI webstore refusé (403) ; sens des couleurs repris de résumés.
4. IEC 60073 : texte non lu.
5. Blocs « blanc » et « noir » de la Table 1 : partiellement masqués par le filigrane.
6. Aucune conversion exécutée par un outil de l'acquéreur ; J2 est à la main. Elle a été **rejouée une fois par le relecteur** (conforme : `#E2AA00`, `#FFD000`) ; un second rejeu par deux outils indépendants reste utile. À β 0,45, B < 0 est aussi écrêté.
7. Nuancier officiel RAL (`#E6B019`, `#417E57`) : pages relues par le relecteur le 2026-10-23 ; la **méthode** qui produit ces valeurs d'affichage n'est pas précisée par RAL, elles ne sont donc pas des mesures.
8. Teintes HCT, rapports de contraste, écart `reussi`–`primary` : hors mission (mission 007).
9. MCU `customColor` et visibilité du constructeur à `errorPalette` : besoin n° 1, autre mission.

## 6. Trois points au sol (à vérifier par Pascal sur l'écran)

1. **Jaune d'alerte, quatre candidats côte à côte avec du texte noir :** `#E2AA00` (ISO à β 0,45,
   J2), `#F9A900` (RAL 1003, J3), `#E6B019` (RAL 1003, nuancier officiel, J3b) et `#F6D04D` (ANSI, J4). Le plus sensible : « jaune clair » de la
   spec correspond-il à J4 plutôt qu'à l'ISO ? À voir en plein soleil (critère 6 de la spec).
2. **Vert de réussite, candidats côte à côte :** `#31865C`, `#0F8558`, `#417E57`, `#00843D`, **et à côté
   VertMarculus `#374742`** et un `primary` clair foncé : se distingue-t-il de `primary` sans le
   texte ? Distinct aussi du jaune pour un œil daltonien (WCAG 1.4.1 : l'icône double la couleur).
3. **Jaune sur fond de conteneur sombre (SPEC_2021, niveau 1,0) :** le jaune figé ne passe pas par
   MCU ; vérifier à l'écran qu'il reste lisible à côté d'un `surfaceDim` poussé vers le sombre
   (cas où les sources divergeaient le plus : ambre ISO contre jaune ANSI pâle).

## 7. Question de contrôle

*Quelle décision cette donnée va-t-elle éclairer ?* Le choix, par Pascal, de la teinte de
« réussi » et du jaune d'alerte (avec son noir) dans l'ADR 0002.

## 8. Questions à Pascal (je ne tranche pas)

- « Jaune clair » : ambre ISO (RAL 1003, `#F9A900` ou `#E6B019` du nuancier officiel), jaune ANSI pâle (`#F6D04D`), ou ISO à β élevé
  (hors gamut, écrêté) ?
- Acceptes-tu de payer ISO 3864-1 et l'Annexe E de 3864-4 pour lever les points 1 et 2 de §5 ?
- Garde-t-on un vert publié (R2 `#31865C`/`#0F8558`, R2b `#417E57` du nuancier RAL, ou R3 ANSI
  `#00843D` ; aucun n'est une teinte normative) comme teinte de départ, harmonisé ou non, après
  mesure HCT de la mission 007 ?

## 9. Sources (consultées 2026-10-23)

- ISO 3864-4:2011, Table 1, aperçu : https://cdn.standards.iteh.ai/samples/51000/096ba63848784a5f9bec95746e5b7196/ISO-3864-4-2011.pdf
- Wikipédia, ISO 3864 (RAL, CMYK, hex ; secondaire) : https://en.wikipedia.org/wiki/ISO_3864
- Wikipédia, List of RAL colours (`#F9A800`, `#0F8558` ; secondaire) : https://en.wikipedia.org/wiki/List_of_RAL_colours
- Nuancier RAL, ral-farben.de (primaire pour RAL ; valeurs d'affichage, méthode non précisée ; relu 2026-10-23 par le relecteur) :
  - vue d'ensemble : https://www.ral-farben.de/en/all-ral-colours
  - RAL 1003 : https://www.ral-farben.de/en/colour/ral-classic/ral-1003/9086 (`#E6B019`)
  - RAL 6032 : https://www.ral-farben.de/en/colour/ral-classic/ral-6032/9218 (`#417E57`)
- LinkML valuesets, SafetyColorEnum (ANSI Z535.1 ; secondaire) : https://linkml.io/valuesets/elements/SafetyColorEnum/
- ISO 22324:2022 (résumé de Wikipédia et de iteh) : https://en.wikipedia.org/wiki/ISO_22324 (non lue directement)
- IEC 60073 (métadonnées seulement) : https://reference.globalspec.com/ref/3879665/iec-60073-2002
- MDC-Android `docs/theming/Color.md` : https://github.com/material-components/material-components-android/blob/master/docs/theming/Color.md
- `docs/spec.md` (besoin 4), `docs/adr/0002-couleurs-semantiques.md`
