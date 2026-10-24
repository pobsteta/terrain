package fr.nemeton.terrain.style

/**
 * Dimensions et formes des boutons de Compose Material 3 1.4.0, en dp (`Float`)
 * (`docs/referentiels/style-marculus.md` § 4, une valeur par ligne du tableau). Marculus ne fixe
 * rien au niveau du thème : son style est le défaut M3 (ADR 0006). Les boutons sont des pilules :
 * [FormesTerrain.pilulePourcent].
 */
object BoutonsTerrain {
    val hauteurMin: Float = 40f
    val largeurMin: Float = 58f
    val tailleIcone: Float = 18f
    val espaceIcone: Float = 8f
    val epaisseurTrait: Float = 1f

    /** Début et fin de `Button`, fin de `Button` avec icône. */
    val margeHorizontale: Float = 24f

    /** Haut et bas, tous les boutons. */
    val margeVerticale: Float = 8f

    /** Début de `Button` avec icône. */
    val margeHorizontaleAvecIcone: Float = 16f

    /** Début et fin de `TextButton`, début de `TextButton` avec icône. */
    val margeHorizontaleTexte: Float = 12f

    /** Fin de `TextButton` avec icône. */
    val margeFinTexteAvecIcone: Float = 16f

    /** Élévation de `Button` au repos, puis au survol. */
    val elevation: Float = 0f
    val elevationSurvol: Float = 1f

    /** `OutlinedButton` et `TextButton` : aucune élévation (`elevation = null`). */
    val elevationContourEtTexte: Float? = null

    /** Style du texte : `labelLarge`. */
    val styleTexte: StyleTexte = TypoTerrain.labelLarge

    val zoneTactileBoutonIcone: Float = 48f
    val conteneurBoutonIcone: Float = 40f

    val fabTaille: Float = 56f
    val fabTailleIcone: Float = 24f
    val fabRayon: Float = FormesTerrain.large
    val fabElevation: Float = 6f

    val petitFabTaille: Float = 40f
    val petitFabTailleIcone: Float = 24f
    val petitFabRayon: Float = FormesTerrain.medium

    /** `AssistChip`, `FilterChip`. */
    val puceHauteur: Float = 32f
    val puceTailleIcone: Float = 18f
    val puceRayon: Float = FormesTerrain.small

    /** `Card` et `ElevatedCard`. */
    val carteRayon: Float = FormesTerrain.medium
}
