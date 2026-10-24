package fr.nemeton.terrain.style

/**
 * Un style de texte de Material 3 : [tailleSp] et [interligneSp] en sp, [graisse] au poids
 * CSS/Compose (400 Normal, 500 Medium), [interlettrageSp] en sp. Kotlin pur : ni police ni Compose.
 */
class StyleTexte(
    val tailleSp: Float,
    val interligneSp: Float,
    val graisse: Int,
    val interlettrageSp: Float,
)
