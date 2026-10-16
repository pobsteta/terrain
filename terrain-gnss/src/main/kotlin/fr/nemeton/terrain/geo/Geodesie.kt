package fr.nemeton.terrain.geo

import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.hypot
import kotlin.math.pow
import kotlin.math.sin
import kotlin.math.sqrt

/** Mesures sur des positions WGS 84 voisines (une parcelle, quelques kilomètres). */
object Geodesie {
    private const val R = 6378137.0 // demi-grand axe GRS80 (m)
    private const val E2 = 0.00669438002290 // carré de l'excentricité GRS80

    /**
     * Aire en mètres carrés (toujours positive) d'une (multi)parcelle décrite par ses anneaux.
     * Projection équirectangulaire locale (centrée sur la latitude moyenne) puis formule du lacet :
     * précis à l'échelle d'une parcelle forestière. La somme signée des anneaux gère les trous
     * (anneau extérieur et trous d'orientations opposées selon la norme OGC).
     */
    fun aireM2(anneaux: List<List<Position>>): Double {
        val points = anneaux.flatten()
        if (points.size < 3) return 0.0
        val lat0 = points.sumOf { it.latitude } / points.size
        val cosLat = cos(lat0 * PI / 180.0)
        var somme = 0.0
        for (anneau in anneaux) {
            if (anneau.size < 3) continue
            var aire = 0.0
            for (i in anneau.indices) {
                val a = anneau[i]
                val b = anneau[(i + 1) % anneau.size]
                val xa = a.longitude * PI / 180.0 * R * cosLat
                val ya = a.latitude * PI / 180.0 * R
                val xb = b.longitude * PI / 180.0 * R * cosLat
                val yb = b.latitude * PI / 180.0 * R
                aire += xa * yb - xb * ya
            }
            somme += aire / 2.0
        }
        return abs(somme)
    }

    /** Aire en hectares. */
    fun aireHa(anneaux: List<List<Position>>): Double = aireM2(anneaux) / 10_000.0

    /**
     * Décalage (est, nord) en mètres de [de] vers [vers] : plan local sur l'ellipsoïde GRS80, avec
     * les rayons de courbure méridien et du premier vertical à la latitude moyenne. Erreur bien
     * inférieure au centimètre pour 100 m.
     */
    fun decalage(de: Position, vers: Position): Pair<Double, Double> {
        val phi = Math.toRadians((de.latitude + vers.latitude) / 2)
        val w = sqrt(1 - E2 * sin(phi).pow(2))
        val m = R * (1 - E2) / (w * w * w) // rayon méridien
        val n = R / w // rayon du premier vertical
        val est = Math.toRadians(vers.longitude - de.longitude) * n * cos(phi)
        val nord = Math.toRadians(vers.latitude - de.latitude) * m
        return est to nord
    }

    fun distanceM(de: Position, vers: Position): Double = decalage(de, vers).let { (e, n) -> hypot(e, n) }

    /** Azimut géographique (degrés depuis le nord vrai, sens horaire, dans [0, 360)). */
    fun azimutDeg(de: Position, vers: Position): Double = decalage(de, vers).let { (e, n) ->
        val a = Math.toDegrees(atan2(e, n)).mod(360.0)
        if (a >= 360.0) 0.0 else a
    }
}
