package fr.nemeton.terrain.style

/**
 * Une couleur d'état commune aux applications (ADR 0002) : `reussi` ou `alerte` d'un [Schema],
 * en quatre rôles ARGB 32 bits `0xFFRRGGBB` dans un [Long].
 *
 * [surCouleur] s'écrit sur [couleur], [surConteneur] sur [conteneur]. Toute valeur hors de
 * [`0xFF000000`, `0xFFFFFFFF`] est refusée par [IllegalArgumentException].
 */
class Semantique(
    val couleur: Long,
    val surCouleur: Long,
    val conteneur: Long,
    val surConteneur: Long,
) {
    init {
        listOf(
            "couleur" to couleur, "surCouleur" to surCouleur,
            "conteneur" to conteneur, "surConteneur" to surConteneur,
        ).forEach { (nom, argb) ->
            require(argb in 0xFF000000L..0xFFFFFFFFL) {
                "$nom : ARGB attendu au format 0xFFRRGGBB (alpha FF, 32 bits sans extension de signe) : $argb"
            }
        }
    }
}
