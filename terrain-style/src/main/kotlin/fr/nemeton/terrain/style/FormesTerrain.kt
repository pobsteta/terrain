package fr.nemeton.terrain.style

/**
 * Rayons d'angle de `Shapes` de Compose Material 3 1.4.0, en dp (`docs/referentiels/style-marculus.md`
 * § 2). Marculus ne fixe aucune forme au niveau du thème : son style est le défaut M3 (ADR 0006).
 */
object FormesTerrain {
    val extraSmall: Int = 4
    val small: Int = 8
    val medium: Int = 12
    val large: Int = 16
    val extraLarge: Int = 28

    /** La pilule des boutons : `RoundedCornerShape(50)`, en pourcent (référentiel, écart E4). */
    val pilulePourcent: Int = 50
}
