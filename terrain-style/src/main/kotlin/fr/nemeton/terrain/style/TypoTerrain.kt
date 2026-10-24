package fr.nemeton.terrain.style

/**
 * Les 15 styles de `Typography` de Compose Material 3 1.4.0 (`docs/referentiels/style-marculus.md`
 * § 3). Marculus ne fixe aucune typographie : son style est le défaut M3 (ADR 0006). La famille de
 * police (SansSerif) n'est pas portée ici : le module reste Kotlin pur.
 */
object TypoTerrain {

    /** Un style de texte : taille et interligne en sp, [graisse] CSS/Compose (400, 500), interlettrage en sp. */
    class Style(
        val tailleSp: Int,
        val interligneSp: Int,
        val graisse: Int,
        val interlettrageSp: Double,
    )

    val displayLarge = Style(57, 64, 400, -0.2)
    val displayMedium = Style(45, 52, 400, 0.0)
    val displaySmall = Style(36, 44, 400, 0.0)
    val headlineLarge = Style(32, 40, 400, 0.0)
    val headlineMedium = Style(28, 36, 400, 0.0)
    val headlineSmall = Style(24, 32, 400, 0.0)
    val titleLarge = Style(22, 28, 400, 0.0)
    val titleMedium = Style(16, 24, 500, 0.2)
    val titleSmall = Style(14, 20, 500, 0.1)
    val bodyLarge = Style(16, 24, 400, 0.5)
    val bodyMedium = Style(14, 20, 400, 0.2)
    val bodySmall = Style(12, 16, 400, 0.4)
    val labelLarge = Style(14, 20, 500, 0.1)
    val labelMedium = Style(12, 16, 500, 0.5)
    val labelSmall = Style(11, 16, 500, 0.5)
}
