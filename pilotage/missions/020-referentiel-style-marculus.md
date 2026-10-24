# Mission 020 — référentiel du style de Marculus

> Fiche de mission (fiches n° 9 et 15 de la méthode). Une mission = une phrase = une
> branche = une PR. Tirée de l'ADR 0006 (cap de Pascal du 2026-10-24 : le style de Marculus est la règle).

## Mission

**En une phrase (quoi, et pourquoi) :** Relever dans Marculus (tag v0.65.0) et dans Compose Material 3 (BOM `2026.02.01` de Marculus) les 48 rôles de couleur en clair et en sombre, les `Shapes`, la `Typography` et les dimensions et formes des boutons de Marculus, puis mesurer l'écart de teinte HCT entre `reussi` et `VertVif`, dans `docs/referentiels/style-marculus.md` et `terrain-style/src/test/resources/jetons/style-marculus.csv`, pour que `terrain-style` recopie le style de Marculus sans rien deviner (ADR 0006).

**Redite par l'agent :** _à compléter par l'agent avant d'agir._

## Les trois questions

| Question | Réponse |
|---|---|
| Redire la mission ? | oui |
| Sait-il comment faire ? | oui : sources nommées ci-dessous |
| A-t-il les moyens ? | oui : le dépôt Marculus est en local, et les jars Material 3 sont dans le cache Gradle ou sur Maven (Google) |

**Temps de la main :** appuyer.

## L'Homme

- **Agent :** `dev` (il lui faut un shell), sonnet
- **Ce qui ne lui est pas demandé :** modifier le code de `terrain-style` ou ses tests ; toucher à Marculus.

## Harnais

- **Peut :** lire `/home/pascal/dev/marculus` (lecture seule, au tag `v0.65.0` par `git show v0.65.0:<chemin>`) ; télécharger les sources ou les jars `androidx.compose.material3` à la version que donne le BOM `2026.02.01`, hors du dépôt ; écrire `docs/referentiels/style-marculus.md` et le CSV ; ouvrir une PR.
- **Ne doit pas :** inventer une valeur ; prendre une valeur de mémoire ; versionner un binaire.
- **Sources :**
  - Marculus : `app/src/main/kotlin/io/github/pobsteta/marculus/ui/theme/Theme.kt`, et tout l'`app/src/main` pour les formes, la typographie et les boutons ;
  - `gradle/libs.versions.toml` de Marculus, pour le BOM ;
  - Material 3 : `ColorLightTokens`, `ColorDarkTokens`, `lightColorScheme()`, `darkColorScheme()`, `ShapeDefaults` ou `ShapeTokens`, `TypographyTokens` et `TypeScaleTokens`, `ButtonDefaults`, à la version exacte.

## Valeur provisoire

Aucune : la mission relève.

## Dépend de / rouge attendu jusqu'à

Dépend de : rien. CI rouge attendue : aucune.

## Tests

- **Comment saura-t-on que c'est juste :**
  - chaque valeur a sa source (fichier, ligne, version), et la commande ou le chemin du jar qui la donne ;
  - les 48 rôles, les 5 `Shapes`, les 15 styles de `Typography` et chaque dimension de bouton relevée sont listés en entier ;
  - le CSV a pour colonnes `theme,role,argb`, 96 lignes de couleurs (48 par thème), et il est lisible par un successeur ;
  - pour chaque usage de forme ou de dimension dans Marculus, le référentiel dit si c'est un style commun (bouton, carte, pilule) ou une mise en page propre à un écran ;
  - l'écart de teinte HCT entre `reussi.couleur` et `primary` est donné en clair et en sombre, avec sa méthode.

## Terrain

- **Échantillon que Pascal vérifiera :** le tableau des cinq rôles de Marculus et du `primary` : est-ce bien le vert de Marculus ?

## Ma réponse d'abord

_Rempli par Pascal seul, avant de lire le travail de l'agent. L'orchestrateur ne le remplit pas._

## Qui veille, qui tranche

- **Relecteur :** `relecteur`, puis Pascal.
- **Critère d'arrêt de la relecture :** 3 allers-retours ; au-delà, la décision remonte à Pascal.
- **Point de suivi :** à la PR.
- **Qui tranche :** Pascal.
