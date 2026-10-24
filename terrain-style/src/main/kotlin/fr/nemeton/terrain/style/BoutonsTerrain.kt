package fr.nemeton.terrain.style

/**
 * Dimensions et formes des boutons de Compose Material 3 1.4.0, en dp (`docs/referentiels/style-marculus.md`
 * § 4, une propriété par ligne du tableau). Marculus ne fixe rien au niveau du thème : son style
 * est le défaut M3 (ADR 0006). Les boutons sont des pilules ([formePilulePourcent]).
 */
object BoutonsTerrain {

    /** Marges intérieures en dp : début, haut, fin, bas. */
    class Marges(val debutDp: Int, val hautDp: Int, val finDp: Int, val basDp: Int)

    val hauteurMinDp: Int = 40
    val largeurMinDp: Int = 58

    /** `Button`, `OutlinedButton`, `ElevatedButton`, `FilledTonalButton`. */
    val margesBouton = Marges(24, 8, 24, 8)
    val margesBoutonAvecIcone = Marges(16, 8, 24, 8)

    /** `TextButton`. */
    val margesBoutonTexte = Marges(12, 8, 12, 8)
    val margesBoutonTexteAvecIcone = Marges(12, 8, 16, 8)

    /** Taille de l'icône dans un bouton (`ButtonDefaults.IconSize`). */
    val iconeDp: Int = 18
    val espaceIconeTexteDp: Int = 8
    val formePilulePourcent: Int = FormesTerrain.pilulePourcent

    /** Trait de `OutlinedButton`. */
    val traitContourDp: Int = 1

    /** Élévation de `Button` au repos, puis au survol. */
    val elevationDp: Int = 0
    val elevationSurvolDp: Int = 1

    /** `OutlinedButton` et `TextButton` : aucune élévation (`elevation = null`). */
    val elevationContourEtTexteDp: Int? = null

    /** Style du texte : `labelLarge`. */
    val styleTexte: TypoTerrain.Style = TypoTerrain.labelLarge

    val iconBoutonZoneTactileDp: Int = 48
    val iconBoutonConteneurDp: Int = 40

    val fabDp: Int = 56
    val fabIconeDp: Int = 24
    val fabRayonDp: Int = FormesTerrain.large
    val fabElevationDp: Int = 6

    val petitFabDp: Int = 40
    val petitFabIconeDp: Int = 24
    val petitFabRayonDp: Int = FormesTerrain.medium

    /** `AssistChip`, `FilterChip`. */
    val puceHauteurDp: Int = 32
    val puceIconeDp: Int = 18
    val puceRayonDp: Int = FormesTerrain.small

    /** `Card` et `ElevatedCard`. */
    val carteRayonDp: Int = FormesTerrain.medium
}
