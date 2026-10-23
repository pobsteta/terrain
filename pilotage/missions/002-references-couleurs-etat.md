# Mission 002 — references-couleurs-etat

> Fiche de mission (fiches n° 9 et 15 de la méthode). Une mission = une phrase = une
> branche = une PR. Tirée de `docs/spec.md` (validée le 2026-10-23), issue #1.

## Mission

**En une phrase (quoi, et pourquoi) :** Rassembler et comparer les conventions publiées des couleurs d'état (teinte de « réussi », jaune de sécurité et sa couleur de contraste, en sRGB sourcé), pour que Pascal choisisse les valeurs de l'ADR 0002.

**Redite par l'agent :** Réunir, avec leurs sources et leurs écarts, les valeurs sRGB publiées de « réussi » (vert) et du jaune de sécurité avec son noir, sans en choisir aucune, pour que Pascal fixe les valeurs de l'ADR 0002 en une lecture.

## Les trois questions (règle de staffing)

| Question | Réponse | Conséquence |
|---|---|---|
| Peut-il redire la mission en une phrase, avec son but ? | oui | — |
| Sait-il comment faire ? (sources, conventions nommées ci-dessous) | oui : besoin 4 de la spec, ADR 0002 (ISO 3864-1, ISO 3864-4, ISO 22324, IEC 60073, MDC « custom colors ») | — |
| A-t-il les moyens ? (outils, données d'essai, tests) | oui pour les sources publiques ; les normes ISO sont payantes : l'agent dit ce qu'il n'a pas pu lire | — |

**Temps de la main :** appuyer — la mission prépare un choix de valeur de Pascal.

## Ma réponse d'abord

_Rempli par Pascal seul, avant de lire le travail de l'agent : ce qu'il attend de la
mission, et le résultat qu'il prévoit. Une ligne suffit. L'orchestrateur ne le remplit
pas._

## L'Homme

- **Agent :** `acquereur`
- **Modèle :** opus (justesse des données)
- **Ce qui ne lui est pas demandé :** choisir la teinte ou le jaune ; calculer des teintes HCT ou des contrastes (la génération, mission 007, les mesure).

## Harnais

- **Peut :** écrire `docs/referentiels/couleurs-etat.md`, ouvrir une PR.
- **Ne doit pas :** toucher à `main`, sortir du périmètre, inventer une valeur ou une source, trancher une question de valeur (la poser à Pascal)
- **Sources autorisées :** `docs/spec.md` (besoin 4), `docs/adr/0002-*.md`, pages publiques ISO/IEC, aperçus, MDC-Android `Color.md`, sources secondaires signalées comme telles.

## Tests

- **Comment saura-t-on que c'est juste :** au moins deux sources comparées par valeur ; chaque valeur sRGB avec sa source et sa méthode de conversion (illuminant, espace) ; ce qui n'a pas pu être vérifié, listé.

## Terrain

- **Échantillon que Pascal vérifiera :** les candidats « jaune clair + noir » et « vert de réussite », vus côte à côte sur un écran.

## Qui veille, qui tranche

- **Relecteur :** `relecteur`, puis Pascal.
- **Critère d'arrêt de la relecture :** 3 allers-retours ; au-delà, la décision remonte à
  Pascal.
- **Point de suivi :** à la PR, puis au moment où Pascal choisit les valeurs de l'ADR 0002.
- **Qui tranche :** Pascal.

## Comment saura-t-on que ça marche ?

Pascal choisit la teinte de réussi et le jaune en une lecture, sans nouvelle recherche.

## Remarques du relecteur — aller-retour 1/3 (2026-10-23)

À reprendre dans `docs/referentiels/couleurs-etat.md`, sans rien trancher :

1. **Bloquant : `#237F52`** (« autre copie » de RAL 6032) n'a pas de source retrouvée :
   ni Wikipédia « ISO 3864 », ni Wikipédia « List of RAL colours » (qui donne `#0F8558`
   pour RAL 6032). Citer sa source ou le retirer, y compris de l'échantillon §6.2.
2. **Citer en §9** la source de `#F9A800` : Wikipédia, « List of RAL colours ».
3. **Ligne J2** : à β 0,70, l'écrêtage déplace la couleur (`#FFD000` ≈ β 0,66,
   chromaticité ≈ (0,457 ; 0,475), toujours dans la région) ; à β 0,45, B < 0 est aussi
   écrêté ; noter en §5 que la conversion a été rejouée une fois par le relecteur
   (conforme : `#E2AA00`, `#FFD000`).
4. **Recommandé : nuancier officiel RAL** (ral-farben.de), source primaire : `#E6B019`
   pour 1003, `#417E57` pour 6032, à ajouter comme valeur la plus primaire.
5. **Utile, à ton jugement** : retour hex → chromaticité (écran ≈ couleur d'objet sous
   D65, approximatif) : `#F6D04D` (ANSI) hors de la région jaune ISO ; `#31865C` et
   `#00843D` hors de la région verte ISO ; `#0F8558` dedans. Si tu l'ajoutes, refais le
   calcul ou cite-le comme calcul du relecteur.
6. **§4** : écrire l'effet de l'harmonisation comme « hypothèse à mesurer » (écart HSL ≠
   rotation HCT), pas « rapprocherait déjà ».
7. Remplir « Redite par l'agent » en tête de fiche.

## Remarques du relecteur — aller-retour 2/3 (2026-10-23)

Points 1 à 7 traités ; quatre retouches mineures dans `docs/referentiels/couleurs-etat.md` :

1. **Inscrire les URL du nuancier RAL** (relues par le relecteur le 2026-10-23, réponse 200)
   en §9, J3b et R2b, et remplacer « rapporté par le relecteur, non relue » par
   « relue 2026-10-23 (relecteur) » ; retirer ou reformuler le point 9 de §5 :
   - vue d'ensemble : https://www.ral-farben.de/en/all-ral-colours
   - RAL 1003 : https://www.ral-farben.de/en/colour/ral-classic/ral-1003/9086 → `#E6B019`
   - RAL 6032 : https://www.ral-farben.de/en/colour/ral-classic/ral-6032/9218 → `#417E57`
   - Fiabilité : couleur d'affichage des pastilles du site, **méthode non précisée** par
     RAL ; ne pas la présenter comme une mesure.
2. **Concordance §4** : « R2 et R3 (≈ 148° à 151°) concordent » n'est plus exact ; donner
   l'étendue réelle (142° à 157° avec R2 et R2b).
3. **Échantillon §6.1 et question §8** : ajouter `#E6B019` au côte à côte jaune, et le citer
   en §8 à côté de `#F9A900`, pour que les deux côtés suivent la même règle.
4. **Numérotation de §5** : remettre dans l'ordre (aujourd'hui 1-6, 9, 7, 8).
