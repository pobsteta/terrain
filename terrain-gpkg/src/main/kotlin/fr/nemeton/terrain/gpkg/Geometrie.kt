package fr.nemeton.terrain.gpkg

import java.nio.ByteBuffer
import java.nio.ByteOrder

/**
 * Géométrie d'une entité GeoPackage, dans le système de sa couche (Z et M lus puis ignorés :
 * la carte et les calculs de terrain sont en 2D). Les « multi » et les collections deviennent
 * une [Collection] de leurs parties.
 */
sealed interface Geometrie {
    data class Point(val x: Double, val y: Double) : Geometrie

    data class Ligne(val points: List<Point>) : Geometrie

    /** Anneau extérieur d'abord, puis les trous. */
    data class Polygone(val anneaux: List<List<Point>>) : Geometrie

    data class Collection(val parties: List<Geometrie>) : Geometrie

    /** Anneaux de tous les polygones (les lignes et les points sont ignorés). */
    fun anneaux(): List<List<Point>> = when (this) {
        is Polygone -> anneaux
        is Collection -> parties.flatMap { it.anneaux() }
        is Point, is Ligne -> emptyList()
    }

    /** Lignes, et contours des polygones (une route large peut être cartographiée en surface). */
    fun lignes(): List<List<Point>> = when (this) {
        is Ligne -> listOf(points)
        is Polygone -> anneaux
        is Collection -> parties.flatMap { it.lignes() }
        is Point -> emptyList()
    }

    /** Tous les points, dans l'ordre. */
    fun points(): List<Point> = when (this) {
        is Point -> listOf(this)
        is Ligne -> points
        is Polygone -> anneaux.flatten()
        is Collection -> parties.flatMap { it.points() }
    }
}

/**
 * Format binaire des géométries GeoPackage (en-tête `GP` + WKB ISO, norme OGC 12-128r18 § 2.1.3).
 * Lit les enveloppes de toutes tailles, les deux boutismes, Z / M / ZM en ISO (types 1001…3007)
 * comme en EWKB (bits de poids fort) ; écrit sans enveloppe, en petit-boutiste, en 2D.
 */
object FormatGpkg {
    /** Géométrie d'un blob GeoPackage, ou null pour une géométrie vide ; erreur si illisible. */
    fun decoder(blob: ByteArray): Geometrie? {
        require(blob.size >= 8 && blob[0] == 'G'.code.toByte() && blob[1] == 'P'.code.toByte()) {
            "En-tête GeoPackage absent"
        }
        val drapeaux = blob[3].toInt()
        val vide = drapeaux and 0x10 != 0
        val tailleEnveloppe = when ((drapeaux shr 1) and 0x07) {
            0 -> 0
            1 -> 32
            2, 3 -> 48
            4 -> 64
            else -> throw IllegalArgumentException("Enveloppe GeoPackage inconnue")
        }
        if (vide) return null
        val wkb = ByteBuffer.wrap(blob, 8 + tailleEnveloppe, blob.size - 8 - tailleEnveloppe)
        return lireWkb(wkb)
    }

    /** Code SRS de l'en-tête d'un blob GeoPackage. */
    fun srsId(blob: ByteArray): Int {
        val ordre = if (blob[3].toInt() and 0x01 != 0) ByteOrder.LITTLE_ENDIAN else ByteOrder.BIG_ENDIAN
        return ByteBuffer.wrap(blob, 4, 4).order(ordre).int
    }

    /** Blob GeoPackage (sans enveloppe, petit-boutiste, 2D) de [g] dans le système [srsId]. */
    fun encoder(g: Geometrie, srsId: Int): ByteArray {
        val corps = mutableListOf<ByteArray>()
        ecrireWkb(g, corps)
        val wkb = corps.fold(ByteArray(0)) { a, b -> a + b }
        val entete = ByteBuffer.allocate(8).order(ByteOrder.LITTLE_ENDIAN)
        entete.put('G'.code.toByte()).put('P'.code.toByte()).put(0).put(0x01).putInt(srsId)
        return entete.array() + wkb
    }

    private fun lireWkb(b: ByteBuffer): Geometrie {
        b.order(if (b.get().toInt() == 1) ByteOrder.LITTLE_ENDIAN else ByteOrder.BIG_ENDIAN)
        val brut = b.int
        val ewkbZ = brut and 0x80000000.toInt() != 0
        val ewkbM = brut and 0x40000000 != 0
        val code = brut and 0x0FFFFFFF
        val type = code % 1000
        val dimensions = 2 + when (code / 1000) {
            1, 2 -> 1
            3 -> 2
            else -> 0
        } + (if (ewkbZ) 1 else 0) + (if (ewkbM) 1 else 0)

        fun point(): Geometrie.Point {
            val x = b.double
            val y = b.double
            repeat(dimensions - 2) { b.double }
            return Geometrie.Point(x, y)
        }
        fun suite(): List<Geometrie.Point> = List(b.int) { point() }

        return when (type) {
            1 -> point()
            2 -> Geometrie.Ligne(suite())
            3 -> Geometrie.Polygone(List(b.int) { suite() })
            4, 5, 6, 7 -> Geometrie.Collection(List(b.int) { lireWkb(b) })
            else -> throw IllegalArgumentException("Type WKB non géré : $code")
        }
    }

    private fun ecrireWkb(g: Geometrie, sortie: MutableList<ByteArray>) {
        fun entete(type: Int, n: Int?) = ByteBuffer.allocate(if (n == null) 5 else 9).order(ByteOrder.LITTLE_ENDIAN).apply {
            put(1)
            putInt(type)
            if (n != null) putInt(n)
        }.array()
        fun points(p: List<Geometrie.Point>) = ByteBuffer.allocate(4 + 16 * p.size).order(ByteOrder.LITTLE_ENDIAN).apply {
            putInt(p.size)
            p.forEach { putDouble(it.x).putDouble(it.y) }
        }.array()
        when (g) {
            is Geometrie.Point -> sortie += entete(1, null) +
                ByteBuffer.allocate(16).order(ByteOrder.LITTLE_ENDIAN).putDouble(g.x).putDouble(g.y).array()
            is Geometrie.Ligne -> sortie += entete(2, null) + points(g.points)
            is Geometrie.Polygone -> {
                sortie += entete(3, g.anneaux.size)
                g.anneaux.forEach { sortie += points(it) }
            }
            is Geometrie.Collection -> {
                // Type le plus précis possible : MULTIPOINT, MULTILINESTRING, MULTIPOLYGON, sinon collection.
                val type = when {
                    g.parties.isNotEmpty() && g.parties.all { it is Geometrie.Point } -> 4
                    g.parties.isNotEmpty() && g.parties.all { it is Geometrie.Ligne } -> 5
                    g.parties.isNotEmpty() && g.parties.all { it is Geometrie.Polygone } -> 6
                    else -> 7
                }
                sortie += entete(type, g.parties.size)
                g.parties.forEach { ecrireWkb(it, sortie) }
            }
        }
    }
}
