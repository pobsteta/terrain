package fr.nemeton.terrain.carte

import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.floor
import kotlin.math.ln
import kotlin.math.tan

/** Emprise en degrés WGS 84. */
data class Emprise(val ouest: Double, val sud: Double, val est: Double, val nord: Double)

/** Une tuile Web Mercator (schéma XYZ, celui d'osmdroid et du jeu de matrices IGN `PM_0_19`). */
data class Tuile(val z: Int, val x: Int, val y: Int)

/**
 * Tuiles à télécharger pour travailler hors ligne : l'emprise (plus une marge, voir [projet]) de
 * [zoomMin] à [zoomMax]. Repris de Groma.
 */
class ZoneTuiles(val emprise: Emprise, val zoomMin: Int = ZOOM_MIN, val zoomMax: Int = ZOOM_MAX) {
    init {
        require(zoomMin in 0..zoomMax) { "Zooms invalides." }
    }

    /** Plages de colonnes et de rangées au zoom [z]. */
    fun plage(z: Int): Pair<IntRange, IntRange> {
        val (x0, y0) = tuile(emprise.ouest, emprise.nord, z)
        val (x1, y1) = tuile(emprise.est, emprise.sud, z)
        return (x0..x1) to (y0..y1)
    }

    val nombre: Long get() = (zoomMin..zoomMax).sumOf { z -> plage(z).let { (xs, ys) -> xs.count().toLong() * ys.count() } }

    /** Toutes les tuiles, zoom par zoom (les petits zooms d'abord : une carte utile tôt). */
    fun tuiles(): Sequence<Tuile> = sequence {
        for (z in zoomMin..zoomMax) {
            val (xs, ys) = plage(z)
            for (x in xs) for (y in ys) yield(Tuile(z, x, y))
        }
    }

    companion object {
        const val ZOOM_MIN = 12
        const val ZOOM_MAX = 18
        const val MARGE_M = 200.0

        /** Au-delà, le téléchargement est refusé (usage raisonnable de la Géoplateforme : une forêt, pas un département). */
        const val NOMBRE_MAX = 30_000L

        /** Zone d'un chantier : son emprise élargie de [margeM] mètres de chaque côté. */
        fun projet(e: Emprise, margeM: Double = MARGE_M): ZoneTuiles {
            val dLat = margeM / M_PAR_DEGRE
            val dLon = margeM / (M_PAR_DEGRE * cos(Math.toRadians((e.sud + e.nord) / 2)))
            return ZoneTuiles(Emprise(e.ouest - dLon, e.sud - dLat, e.est + dLon, e.nord + dLat))
        }

        /** Tuile (x, y) contenant (lon, lat) au zoom [z]. */
        fun tuile(lon: Double, lat: Double, z: Int): Pair<Int, Int> {
            val n = 1 shl z
            val phi = Math.toRadians(lat.coerceIn(-LAT_MAX, LAT_MAX))
            val x = floor((lon + 180.0) / 360.0 * n).toInt().coerceIn(0, n - 1)
            val y = floor((1.0 - ln(tan(phi) + 1.0 / cos(phi)) / PI) / 2.0 * n).toInt().coerceIn(0, n - 1)
            return x to y
        }

        private const val M_PAR_DEGRE = 111_320.0
        private const val LAT_MAX = 85.05112878
    }
}
