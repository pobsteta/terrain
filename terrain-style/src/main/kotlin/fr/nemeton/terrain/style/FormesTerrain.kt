package fr.nemeton.terrain.style

/**
 * Rayons d'angle de `Shapes` de Compose Material 3 1.4.0, en dp, `Float` (`docs/referentiels/style-marculus.md`
 * § 2). Marculus ne fixe aucune forme au niveau du thème : son style est le défaut M3 (ADR 0006).
 */
object FormesTerrain {
    val extraSmall: Float = 4f
    val small: Float = 8f
    val medium: Float = 12f
    val large: Float = 16f
    val extraLarge: Float = 28f

    /** La pilule des boutons : `RoundedCornerShape(50)`, en pourcent (référentiel, écart E4). */
    val pilulePourcent: Int = 50
}
