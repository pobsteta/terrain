package fr.nemeton.terrain.journal

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class FusionTest {
    private data class S(val id: String, val modifie: Long, val valeur: String)

    private fun aEcrire(entrantes: List<S>, locales: Map<String, Long>) =
        Fusion.aEcrire(entrantes, locales, S::id, S::modifie)

    private fun union(locales: List<S>, entrantes: List<S>) = Fusion.union(locales, entrantes, S::id, S::modifie)

    @Test
    fun `inconnue ajoutee, plus recente remplace, plus ancienne ignoree`() {
        val locales = mapOf("a" to 10L, "b" to 10L)
        val r = aEcrire(listOf(S("a", 11, "a2"), S("b", 9, "b0"), S("c", 1, "c")), locales)
        assertEquals(listOf("a2", "c"), r.map { it.valeur })
    }

    @Test
    fun `egalite - la locale reste`() {
        assertTrue(aEcrire(listOf(S("a", 10, "autre")), mapOf("a" to 10L)).isEmpty())
        assertEquals("locale", union(listOf(S("a", 10, "locale")), listOf(S("a", 10, "autre"))).single().valeur)
    }

    @Test
    fun `doublons entrants - la plus recente une seule fois`() {
        val r = aEcrire(listOf(S("a", 3, "v3"), S("a", 5, "v5"), S("a", 4, "v4")), emptyMap())
        assertEquals(listOf("v5"), r.map { it.valeur })
    }

    @Test
    fun `union idempotente et commutative hors egalites`() {
        val x = listOf(S("a", 1, "a1"), S("b", 5, "b5"))
        val y = listOf(S("a", 2, "a2"), S("b", 4, "b4"), S("c", 1, "c1"))
        val xy = union(x, y).sortedBy { it.id }
        assertEquals(listOf("a2", "b5", "c1"), xy.map { it.valeur })
        assertEquals(xy, union(y, x).sortedBy { it.id })
        assertEquals(xy, union(xy, y).sortedBy { it.id })
    }

    @Test
    fun `fusion n efface rien`() {
        assertEquals(2, union(listOf(S("a", 1, "a"), S("b", 1, "b")), emptyList()).size)
    }

    @Test
    fun `horloge strictement croissante, meme figee ou en recul`() {
        val temps = ArrayDeque(listOf(100L, 100L, 100L, 90L, 200L))
        val h = HorlogeMonotone { temps.removeFirst() }
        assertEquals(listOf(100L, 101L, 102L, 103L, 200L), List(5) { h.maintenant() })
    }

    @Test
    fun `identifiants`() {
        val id = Identifiants.nouveau()
        assertTrue(Identifiants.estValide(id))
        assertEquals(id.lowercase(), id)
        assertTrue(!Identifiants.estValide("pas-un-uuid"))
        assertTrue(Identifiants.nouveau() != id)
    }
}
