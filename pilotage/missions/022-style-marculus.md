# Mission 022 — terrain-style reprend le style de Marculus (v0.7.1)

> Fiche de mission (fiches n° 9 et 15 de la méthode). Une mission = une phrase = une
> branche = une PR. Tirée de l'ADR 0006 (cap de Pascal du 2026-10-24 : le style de Marculus est la règle).

## Mission

**En une phrase (quoi, et pourquoi) :** Remplacer dans `terrain-style` les 48 rôles générés par les valeurs du référentiel `docs/referentiels/style-marculus.md`, ajouter `FormesTerrain`, `TypoTerrain` et `BoutonsTerrain`, garder `reussi`, `alerte` et `Contraste`, régénérer le nuancier, et mettre à jour README et KDoc, jusqu'à faire passer les tests de 021, pour publier en v0.7.1 un `terrain-style` qui est le style de Marculus (ADR 0006).

**Redite par l'agent :** _à compléter par l'agent avant d'agir._

**Temps de la main :** appuyer. C'est une décision de l'orchestrateur, prise pour aller vite, sur la consigne de Pascal du 2026-10-24 : l'API ne fait que s'enrichir, et les valeurs viennent d'un relevé.

## L'Homme

- **Agent :** `dev`, sonnet
- **Ce qui ne lui est pas demandé :** modifier une assertion de 021 ; poser le tag ; toucher à `terrain-voix`.

## Harnais

- **Peut :**
  - modifier `terrain-style/src/main`, `terrain-style/generation/` et la CI de génération ;
  - retirer de la CI la régénération MCU des 48 rôles ;
  - garder le mécanisme de `reussi` s'il sert encore, ou figer ses valeurs de v0.6.1 ;
  - produire `terrain-style/generation/nuancier.svg` depuis les nouvelles valeurs ;
  - mettre à jour le README (section `terrain-style`, coordonnées `v0.7.1`, marche à suivre « Changer le style » : Marculus, puis le référentiel, puis le code) ;
  - mettre à jour la KDoc ;
  - pousser sur la branche de 021.
- **Ne doit pas :** ajouter de dépendance au build publié ; utiliser Compose ou Android (Kotlin pur, JVM 17) ; inventer une valeur absente du référentiel.
- **Sources :** spec, ADR 0006, référentiel et CSV de 020, tests de 021.

## Valeur provisoire

Aucune.

## Dépend de / rouge attendu jusqu'à

- **Dépend de :** 020 et 021 ; part de la branche de 021.
- **Ordre de fusion :** groupée 020 → 021 → 022.
- **CI rouge attendue :** aucune à la fin de la mission.

## Tests

- **Comment saura-t-on que c'est juste :** les tests de 021 passent sans modification ; `./gradlew test` est vert ; le nuancier montre `VertVif` en `primary` et `VertMarculus` en `primaryContainer` ; `grep -ri "7:1\|CONTENT\|#202F2B"` ne trouve plus de règle en vigueur dans le README et la KDoc.

## Terrain

- **Échantillon que Pascal vérifiera :** le nuancier `nuancier.svg`, en clair et en sombre : « c'est le vert de Marculus ». C'est la condition du tag v0.7.1.

## Ma réponse d'abord

_Rempli par Pascal seul, avant de lire le travail de l'agent. L'orchestrateur ne le remplit pas._

## Qui veille, qui tranche

- **Relecteur :** `relecteur`, puis Pascal.
- **Critère d'arrêt de la relecture :** 3 allers-retours ; au-delà, la décision remonte à Pascal.
- **Point de suivi :** à la PR.
- **Qui tranche :** Pascal.
