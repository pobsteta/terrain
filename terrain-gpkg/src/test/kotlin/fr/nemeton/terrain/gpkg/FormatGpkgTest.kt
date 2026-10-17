package fr.nemeton.terrain.gpkg

import java.nio.ByteBuffer
import java.nio.ByteOrder
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNull

class FormatGpkgTest {
    private val p = Geometrie.Point(1.5, -2.25)

    @Test
    fun `aller-retour de toutes les formes`() {
        val formes = listOf(
            p,
            Geometrie.Ligne(listOf(p, Geometrie.Point(3.0, 4.0))),
            Geometrie.Polygone(listOf(listOf(p, Geometrie.Point(3.0, 4.0), Geometrie.Point(5.0, 1.0), p))),
            Geometrie.Collection(listOf(p, Geometrie.Point(0.0, 0.0))),
            Geometrie.Collection(listOf(Geometrie.Ligne(listOf(p, p)))),
            Geometrie.Collection(listOf(p, Geometrie.Ligne(listOf(p, p)))),
        )
        formes.forEach { g ->
            val blob = FormatGpkg.encoder(g, 2154)
            assertEquals(g, FormatGpkg.decoder(blob))
            assertEquals(2154, FormatGpkg.srsId(blob))
        }
    }

    @Test
    fun `gros-boutiste, EWKB Z et geometrie vide`() {
        // En-tête petit-boutiste sans enveloppe, WKB gros-boutiste POINT avec drapeau EWKB Z.
        val wkb = ByteBuffer.allocate(1 + 4 + 24).order(ByteOrder.BIG_ENDIAN)
            .put(0).putInt(0x80000001.toInt()).putDouble(1.5).putDouble(-2.25).putDouble(99.0).array()
        val entete = byteArrayOf('G'.code.toByte(), 'P'.code.toByte(), 0, 0x01, 0x6A, 0x08, 0, 0)
        assertEquals(p, FormatGpkg.decoder(entete + wkb))
        val vide = byteArrayOf('G'.code.toByte(), 'P'.code.toByte(), 0, 0x11, 0x6A, 0x08, 0, 0)
        assertNull(FormatGpkg.decoder(vide))
    }

    @Test
    fun `blob illisible refuse`() {
        assertFailsWith<IllegalArgumentException> { FormatGpkg.decoder(byteArrayOf(1, 2, 3, 4, 5, 6, 7, 8)) }
    }
}
