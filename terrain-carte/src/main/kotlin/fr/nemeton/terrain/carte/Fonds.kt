package fr.nemeton.terrain.carte

/** Mentions exigées par les licences des fonds, à afficher sur la carte. */
object Attributions {
    const val IGN = "© IGN Géoplateforme, Licence Ouverte Etalab 2.0"
    const val OSM = "© contributeurs OpenStreetMap (ODbL)"
    const val CADASTRE = "cadastre © IGN"
}

/**
 * Couche WMTS de la Géoplateforme IGN (`data.geopf.fr`, gratuite, sans clé, Licence Ouverte
 * Etalab 2.0). Le jeu de matrices `PM_0_19` est la grille Web Mercator XYZ d'osmdroid : TILEROW /
 * TILECOL sont directement Y / X. [nom] identifie la source (et son cache) côté application.
 */
data class CoucheWmts(
    val nom: String,
    val couche: String,
    val style: String,
    val format: String,
    val attribution: String,
    val zoomMin: Int = 0,
    val zoomMax: Int = 19,
) {
    /** Extension des fichiers de tuiles, pour le cache. */
    val extension: String get() = if (format == "image/png") ".png" else ".jpg"

    /** URL de la tuile (zoom [z], colonne [x], rangée [y]). */
    fun url(z: Int, x: Int, y: Int): String =
        "$BASE?SERVICE=WMTS&REQUEST=GetTile&VERSION=1.0.0" +
            "&LAYER=$couche&STYLE=${style.replace(" ", "%20")}&FORMAT=$format" +
            "&TILEMATRIXSET=PM_0_19&TILEMATRIX=$z&TILEROW=$y&TILECOL=$x"

    companion object {
        const val BASE = "https://data.geopf.fr/wmts"
    }
}

/**
 * Couches de la Géoplateforme employées par les applications de terrain. Elles peuvent être
 * téléchargées pour le hors-ligne (usage raisonnable, voir [ZoneTuiles.NOMBRE_MAX]) ; les tuiles
 * OpenStreetMap, non : leur politique interdit le téléchargement en masse.
 */
object Geoplateforme {
    /** Plan IGN v2 : le fond par défaut, lisible en forêt (routes, lieux-dits, courbes). */
    val PLAN_IGN = CoucheWmts("PlanIGN", "GEOGRAPHICALGRIDSYSTEMS.PLANIGNV2", "normal", "image/png", Attributions.IGN)

    /** Orthophotos IGN (BD ORTHO®, 20 cm). */
    val ORTHO_IGN = CoucheWmts("OrthoIGN", "ORTHOIMAGERY.ORTHOPHOTOS", "normal", "image/jpeg", Attributions.IGN)

    /** Parcellaire Express (cadastre), transparent : se superpose à n'importe quel fond. */
    val CADASTRE = CoucheWmts("CadastreIGN", "CADASTRALPARCELS.PARCELLAIRE_EXPRESS", "PCI vecteur", "image/png", Attributions.IGN)

    val TOUTES = listOf(PLAN_IGN, ORTHO_IGN, CADASTRE)

    /** Couche d'après son [CoucheWmts.nom] (dossier de tuiles téléchargées, préférence…). */
    fun parNom(nom: String): CoucheWmts? = TOUTES.firstOrNull { it.nom == nom }
}
