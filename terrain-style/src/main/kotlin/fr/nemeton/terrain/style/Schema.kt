package fr.nemeton.terrain.style

/**
 * Un schéma de couleurs : un champ par rôle de `ColorScheme` de Compose Material 3 (1.4.0,
 * 48 rôles, même ordre), en ARGB 32 bits `0xFFRRGGBB` dans un [Long].
 *
 * Toute valeur hors de [`0xFF000000`, `0xFFFFFFFF`] est refusée par
 * [IllegalArgumentException]. Les valeurs de [StyleTerrain] sont celles du style de Marculus
 * (ADR 0006), recopiées du référentiel, jamais retouchées.
 *
 * [reussi] et [alerte] sont les deux couleurs d'état communes (ADR 0002) ; l'erreur est `error*`.
 *
 * [interditsEnTexte] nomme les rôles qui ne servent pas en texte : `alerte.couleur`, par décision
 * (ADR 0002) ; le jaune sert de fond ou de décor. Le style n'impose aucun contraste minimal
 * (ADR 0006). Une couleur d'état s'y nomme `reussi.couleur`, `alerte.conteneur`…
 */
class Schema(
    val primary: Long,
    val onPrimary: Long,
    val primaryContainer: Long,
    val onPrimaryContainer: Long,
    val inversePrimary: Long,
    val secondary: Long,
    val onSecondary: Long,
    val secondaryContainer: Long,
    val onSecondaryContainer: Long,
    val tertiary: Long,
    val onTertiary: Long,
    val tertiaryContainer: Long,
    val onTertiaryContainer: Long,
    val background: Long,
    val onBackground: Long,
    val surface: Long,
    val onSurface: Long,
    val surfaceVariant: Long,
    val onSurfaceVariant: Long,
    val surfaceTint: Long,
    val inverseSurface: Long,
    val inverseOnSurface: Long,
    val error: Long,
    val onError: Long,
    val errorContainer: Long,
    val onErrorContainer: Long,
    val outline: Long,
    val outlineVariant: Long,
    val scrim: Long,
    val surfaceBright: Long,
    val surfaceDim: Long,
    val surfaceContainer: Long,
    val surfaceContainerHigh: Long,
    val surfaceContainerHighest: Long,
    val surfaceContainerLow: Long,
    val surfaceContainerLowest: Long,
    val primaryFixed: Long,
    val primaryFixedDim: Long,
    val onPrimaryFixed: Long,
    val onPrimaryFixedVariant: Long,
    val secondaryFixed: Long,
    val secondaryFixedDim: Long,
    val onSecondaryFixed: Long,
    val onSecondaryFixedVariant: Long,
    val tertiaryFixed: Long,
    val tertiaryFixedDim: Long,
    val onTertiaryFixed: Long,
    val onTertiaryFixedVariant: Long,
    val reussi: Semantique,
    val alerte: Semantique,
    val interditsEnTexte: Set<String>,
) {
    init {
        listOf(
            "primary" to primary, "onPrimary" to onPrimary,
            "primaryContainer" to primaryContainer, "onPrimaryContainer" to onPrimaryContainer,
            "inversePrimary" to inversePrimary,
            "secondary" to secondary, "onSecondary" to onSecondary,
            "secondaryContainer" to secondaryContainer, "onSecondaryContainer" to onSecondaryContainer,
            "tertiary" to tertiary, "onTertiary" to onTertiary,
            "tertiaryContainer" to tertiaryContainer, "onTertiaryContainer" to onTertiaryContainer,
            "background" to background, "onBackground" to onBackground,
            "surface" to surface, "onSurface" to onSurface,
            "surfaceVariant" to surfaceVariant, "onSurfaceVariant" to onSurfaceVariant,
            "surfaceTint" to surfaceTint,
            "inverseSurface" to inverseSurface, "inverseOnSurface" to inverseOnSurface,
            "error" to error, "onError" to onError,
            "errorContainer" to errorContainer, "onErrorContainer" to onErrorContainer,
            "outline" to outline, "outlineVariant" to outlineVariant, "scrim" to scrim,
            "surfaceBright" to surfaceBright, "surfaceDim" to surfaceDim,
            "surfaceContainer" to surfaceContainer, "surfaceContainerHigh" to surfaceContainerHigh,
            "surfaceContainerHighest" to surfaceContainerHighest,
            "surfaceContainerLow" to surfaceContainerLow, "surfaceContainerLowest" to surfaceContainerLowest,
            "primaryFixed" to primaryFixed, "primaryFixedDim" to primaryFixedDim,
            "onPrimaryFixed" to onPrimaryFixed, "onPrimaryFixedVariant" to onPrimaryFixedVariant,
            "secondaryFixed" to secondaryFixed, "secondaryFixedDim" to secondaryFixedDim,
            "onSecondaryFixed" to onSecondaryFixed, "onSecondaryFixedVariant" to onSecondaryFixedVariant,
            "tertiaryFixed" to tertiaryFixed, "tertiaryFixedDim" to tertiaryFixedDim,
            "onTertiaryFixed" to onTertiaryFixed, "onTertiaryFixedVariant" to onTertiaryFixedVariant,
        ).forEach { (nom, argb) ->
            require(argb in 0xFF000000L..0xFFFFFFFFL) {
                "$nom : ARGB attendu au format 0xFFRRGGBB (alpha FF, 32 bits sans extension de signe) : $argb"
            }
        }
    }
}
