package fr.nemeton.terrain.style

/**
 * Les 15 styles de `Typography` de Compose Material 3 1.4.0 (`docs/referentiels/style-marculus.md`
 * § 3). Marculus ne fixe aucune typographie : son style est le défaut M3 (ADR 0006). La famille de
 * police (SansSerif) n'est pas portée ici : le module reste Kotlin pur.
 */
object TypoTerrain {

    val displayLarge: StyleTexte = StyleTexte(57f, 64f, 400, -0.2f)
    val displayMedium = StyleTexte(45f, 52f, 400, 0.0f)
    val displaySmall = StyleTexte(36f, 44f, 400, 0.0f)
    val headlineLarge = StyleTexte(32f, 40f, 400, 0.0f)
    val headlineMedium = StyleTexte(28f, 36f, 400, 0.0f)
    val headlineSmall = StyleTexte(24f, 32f, 400, 0.0f)
    val titleLarge = StyleTexte(22f, 28f, 400, 0.0f)
    val titleMedium = StyleTexte(16f, 24f, 500, 0.2f)
    val titleSmall = StyleTexte(14f, 20f, 500, 0.1f)
    val bodyLarge = StyleTexte(16f, 24f, 400, 0.5f)
    val bodyMedium = StyleTexte(14f, 20f, 400, 0.2f)
    val bodySmall = StyleTexte(12f, 16f, 400, 0.4f)
    val labelLarge = StyleTexte(14f, 20f, 500, 0.1f)
    val labelMedium = StyleTexte(12f, 16f, 500, 0.5f)
    val labelSmall = StyleTexte(11f, 16f, 500, 0.5f)
}
