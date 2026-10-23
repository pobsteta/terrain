package fr.nemeton.terrain.style

import kotlin.math.abs
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

/**
 * Tests d'acceptation de [Contraste] (mission 003, critères 3 et 5 de docs/spec.md).
 *
 * Écrits sans lire l'implémentation. Les rapports de référence viennent de
 * `contraste/reference.csv`, calculé par deux outils tiers (voir `contraste/fabriquer.py`),
 * jamais par le code testé. Les rapports de référence sont TRONQUÉS à 3 décimales.
 */
class ContrasteTest {

    private class Ligne(val a: Long, val b: Long, val rapport: Double, val outil: String, val version: String)

    private fun table(): List<Ligne> {
        val flux = ContrasteTest::class.java.getResourceAsStream("/contraste/reference.csv")
            ?: error("contraste/reference.csv introuvable dans les ressources de test")
        val lignes = flux.bufferedReader().use { it.readLines() }.filter { it.isNotBlank() }
        assertEquals("argb_a,argb_b,rapport,outil,version", lignes.first(), "en-tête de la table")
        return lignes.drop(1).map {
            val c = it.split(',')
            assertEquals(5, c.size, "colonnes de « $it »")
            Ligne(hex(c[0]), hex(c[1]), c[2].toDouble(), c[3], c[4])
        }
    }

    private fun hex(s: String): Long {
        assertTrue(s.startsWith("0x"), "ARGB en hexadécimal 0x… : $s")
        return java.lang.Long.parseLong(s.drop(2), 16)
    }

    private fun assertProche(attendu: Double, obtenu: Double, tolerance: Double, message: String) =
        assertTrue(abs(attendu - obtenu) <= tolerance, "$message : attendu $attendu, obtenu $obtenu")

    // ---- La table de référence est elle-même sûre ----

    @Test
    fun `la table a deux outils tiers distincts qui donnent la meme valeur sur chaque couple`() {
        val parCouple = table().groupBy { it.a to it.b }
        assertTrue(parCouple.size >= 100, "au moins 100 couples, trouvé ${parCouple.size}")
        for ((couple, lignes) in parCouple) {
            val outils = lignes.map { it.outil }.toSet()
            assertTrue(outils.size >= 2, "deux outils au moins pour $couple, trouvé $outils")
            assertTrue(lignes.all { it.version.isNotBlank() }, "version de l'outil pour $couple")
            assertEquals(1, lignes.map { it.rapport }.toSet().size, "les outils divergent sur $couple : $lignes")
        }
    }

    @Test
    fun `la table couvre les cas limites nommes`() {
        val couples = table().map { it.a to it.b }.toSet()
        assertTrue((0xFFFFFFFFL to 0xFF000000L) in couples, "blanc / noir")
        assertTrue((0xFFFFFFFFL to 0xFF3E7A40L) in couples, "cas de la spec")
        assertTrue(couples.any { it.first == it.second }, "couleur identique")
        assertTrue(couples.any { (it.second to it.first) in couples && it.first != it.second }, "deux ordres du même couple")
    }

    // ---- Critère 3 : le rapport redonne la table ----

    @Test
    fun `rapport redonne chaque ligne de la table de reference tronquee a 3 decimales`() {
        for (l in table()) {
            val r = Contraste.rapport(l.a, l.b)
            // La référence est tronquée : la vraie valeur est dans [ref, ref + 0,001[ (bruit flottant toléré).
            assertTrue(
                r >= l.rapport - 1e-9 && r < l.rapport + 0.001 + 1e-9,
                "${l.outil} ${l.version} : rapport(${l.a.toString(16)}, ${l.b.toString(16)}) attendu ${l.rapport}, obtenu $r",
            )
        }
    }

    @Test
    fun `rapport de blanc sur vert 3E7A40 vaut 5,17 a 0,01 pres`() {
        assertProche(5.17, Contraste.rapport(0xFFFFFFFF, 0xFF3E7A40), 0.01, "blanc / 3E7A40")
    }

    // ---- Cas limites ----

    @Test
    fun `noir sur blanc vaut 21`() {
        assertProche(21.0, Contraste.rapport(0xFF000000, 0xFFFFFFFF), 1e-9, "noir / blanc")
    }

    @Test
    fun `une couleur contre elle-meme vaut 1`() {
        for (c in longArrayOf(0xFF000000, 0xFFFFFFFF, 0xFF3E7A40, 0xFF374742, 0xFFFFFF00)) {
            assertProche(1.0, Contraste.rapport(c, c), 1e-12, "identique ${c.toString(16)}")
        }
    }

    @Test
    fun `l'ordre des arguments est indifferent`() {
        for (l in table()) {
            assertEquals(Contraste.rapport(l.a, l.b), Contraste.rapport(l.b, l.a), 1e-12, "couple ${l.a.toString(16)} ${l.b.toString(16)}")
        }
    }

    @Test
    fun `le rapport reste entre 1 et 21`() {
        for (l in table()) {
            val r = Contraste.rapport(l.a, l.b)
            assertTrue(r >= 1.0 - 1e-12 && r <= 21.0 + 1e-9, "rapport $r hors [1 ; 21]")
        }
    }

    @Test
    fun `le rapport brut de 777777 sur blanc est sous 4,5 et celui de 767676 le tient (seuil sur le rapport brut)`() {
        // Vérifie un seuil sur le rapport brut renvoyé par l'API (la troncature est l'affaire du test de couples).
        assertTrue(Contraste.rapport(0xFF777777, 0xFFFFFFFF) < 4.5, "777777 sur blanc est sous 4,5")
        assertTrue(Contraste.rapport(0xFF767676, 0xFFFFFFFF) >= 4.5, "767676 sur blanc tient 4,5")
    }

    // ---- Luminance (WCAG 2.2, seuil 0,04045) ----

    @Test
    fun `luminance de noir et blanc`() {
        assertEquals(0.0, Contraste.luminance(0xFF000000), 1e-12)
        assertEquals(1.0, Contraste.luminance(0xFFFFFFFF), 1e-12)
    }

    @Test
    fun `luminance des primaires suit les coefficients 0,2126 0,7152 0,0722`() {
        assertEquals(0.2126, Contraste.luminance(0xFFFF0000), 1e-9)
        assertEquals(0.7152, Contraste.luminance(0xFF00FF00), 1e-9)
        assertEquals(0.0722, Contraste.luminance(0xFF0000FF), 1e-9)
    }

    @Test
    fun `luminance d'un gris moyen et d'un vert de reference`() {
        // Valeurs calculées à part (formule WCAG 2.2) : 808080 -> 0,2158605 ; 3E7A40 -> 0,1531337.
        assertEquals(0.21586050, Contraste.luminance(0xFF808080), 1e-7)
        assertEquals(0.15313366, Contraste.luminance(0xFF3E7A40), 1e-7)
    }

    @Test
    fun `luminance de part et d'autre du seuil de linearisation (canal 10 branche lineaire, canal 11 branche puissance)`() {
        // Canal 10 : 10/255 <= 0,04045 -> c / 12,92. Canal 11 : > 0,04045 -> ((c + 0,055) / 1,055)^2,4.
        // Les deux branches diffèrent de 7,7e-6 en 11 : la tolérance 1e-7 les distingue.
        assertEquals(0.003035270, Contraste.luminance(0xFF0A0A0A), 1e-7)
        assertEquals(0.003346536, Contraste.luminance(0xFF0B0B0B), 1e-7)
    }

    @Test
    fun `luminance strictement croissante sur la rampe de gris et dans 0 1`() {
        var precedente = -1.0
        for (v in 0..255) {
            val l = Contraste.luminance(0xFF000000 or (v.toLong() shl 16) or (v.toLong() shl 8) or v.toLong())
            assertTrue(l in 0.0..1.0, "luminance $l hors [0 ; 1] pour le gris $v")
            assertTrue(l > precedente, "luminance non croissante au gris $v")
            precedente = l
        }
    }

    // ---- Entrées refusées : ARGB 32 bits en Long, alpha FF, sans extension de signe ----

    @Test
    fun `extension de signe d'un Int negatif refusee`() {
        val etendu = 0xFFFFFFFF.toInt().toLong() // -1L : 0xFFFFFFFFFFFFFFFF
        assertEquals(-1L, etendu)
        assertFailsWith<IllegalArgumentException> { Contraste.luminance(etendu) }
        assertFailsWith<IllegalArgumentException> { Contraste.rapport(etendu, 0xFF000000) }
        assertFailsWith<IllegalArgumentException> { Contraste.rapport(0xFF000000, etendu) }
        assertFailsWith<IllegalArgumentException> { Contraste.luminance(0xFF3E7A40.toInt().toLong()) }
    }

    @Test
    fun `valeur au-dela de 32 bits refusee`() {
        assertFailsWith<IllegalArgumentException> { Contraste.luminance(0x1_FF000000L) }
        assertFailsWith<IllegalArgumentException> { Contraste.rapport(0x1_FFFFFFFFL, 0xFF000000) }
    }

    @Test
    fun `alpha different de FF refuse (spec, alpha toujours FF)`() {
        // Tranché par Pascal le 2026-10-23, inscrit dans docs/spec.md (Données).
        assertFailsWith<IllegalArgumentException> { Contraste.luminance(0x00FFFFFF) }
        assertFailsWith<IllegalArgumentException> { Contraste.luminance(0x80808080) }
        assertFailsWith<IllegalArgumentException> { Contraste.rapport(0x00FFFFFF, 0xFF000000) }
    }

    @Test
    fun `negatif quelconque refuse`() {
        assertFailsWith<IllegalArgumentException> { Contraste.luminance(Long.MIN_VALUE) }
        assertFailsWith<IllegalArgumentException> { Contraste.rapport(-0x00FFFFFFL, 0xFFFFFFFF) }
    }
}
