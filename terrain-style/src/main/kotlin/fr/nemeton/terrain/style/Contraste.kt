package fr.nemeton.terrain.style

import kotlin.math.pow

/**
 * Contraste de couleurs selon WCAG 2.2 (luminance relative et rapport de contraste).
 *
 * Les couleurs sont des ARGB 32 bits en [Long] (`0xFFRRGGBB`), alpha toujours `FF`, sans
 * extension de signe. Toute autre valeur est refusée par [IllegalArgumentException].
 */
object Contraste {

    private const val SEUIL = 0.04045

    /** Luminance relative WCAG 2.2 de [argb], dans [0 ; 1]. */
    fun luminance(argb: Long): Double {
        require(argb in 0xFF000000L..0xFFFFFFFFL) {
            "ARGB attendu au format 0xFFRRGGBB (alpha FF, 32 bits sans extension de signe) : $argb"
        }
        val r = lineaire(((argb shr 16) and 0xFF).toInt())
        val g = lineaire(((argb shr 8) and 0xFF).toInt())
        val b = lineaire((argb and 0xFF).toInt())
        return 0.2126 * r + 0.7152 * g + 0.0722 * b
    }

    /** Rapport de contraste (L1 + 0,05) / (L2 + 0,05), L1 ≥ L2, dans [1 ; 21]. Non arrondi. */
    fun rapport(a: Long, b: Long): Double {
        val la = luminance(a)
        val lb = luminance(b)
        return (maxOf(la, lb) + 0.05) / (minOf(la, lb) + 0.05)
    }

    private fun lineaire(canal: Int): Double {
        val c = canal / 255.0
        return if (c <= SEUIL) c / 12.92 else ((c + 0.055) / 1.055).pow(2.4)
    }
}
