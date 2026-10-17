package fr.nemeton.terrain.carte

import java.util.Locale
import kotlin.math.cos
import kotlin.math.floor
import kotlin.math.log10
import kotlin.math.pow

/** Barre d'échelle de la carte : résolution au sol, distance « ronde » et libellé. */
object Echelle {
    /** Résolution au sol (m/pixel) en Web Mercator, à [latitude] (°) et au [zoom] (osmdroid, tuiles de 256 px). */
    fun resolutionAuSol(latitude: Double, zoom: Double): Double =
        156543.03392 * cos(Math.toRadians(latitude)) / 2.0.pow(zoom)

    /** Plus grande distance « ronde » (1, 2 ou 5 × 10ⁿ) inférieure ou égale à [metres] ; 0 si [metres] ≤ 0. */
    fun distanceRonde(metres: Double): Double {
        if (metres <= 0.0) return 0.0
        val base = 10.0.pow(floor(log10(metres)))
        val facteur = metres / base
        val joli = when {
            facteur >= 5.0 -> 5.0
            facteur >= 2.0 -> 2.0
            else -> 1.0
        }
        return joli * base
    }

    /** « 200 m », « 1 km », « 2.5 km ». */
    fun libelle(metres: Double): String =
        if (metres >= 1000) {
            (metres / 1000).let { if (it == it.toInt().toDouble()) it.toInt().toString() else String.format(Locale.ROOT, "%.1f", it) } + " km"
        } else {
            "${metres.toInt()} m"
        }
}
