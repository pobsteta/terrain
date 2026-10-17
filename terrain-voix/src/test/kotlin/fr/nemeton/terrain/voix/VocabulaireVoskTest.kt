package fr.nemeton.terrain.voix

import java.io.ByteArrayOutputStream
import java.io.File
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.file.Files
import kotlin.test.Test
import kotlin.test.assertEquals

class VocabulaireVoskTest {
    private val dossier: File = Files.createTempDirectory("modele").toFile()

    /** En-tête OpenFst minimal suivi de la table des symboles d'entrée. */
    private fun ecrireFst(mots: List<String>, drapeaux: Int = 1, magie: Int = 2125659606) {
        val o = ByteArrayOutputStream()
        fun i32(v: Int) = o.write(ByteBuffer.allocate(4).order(ByteOrder.LITTLE_ENDIAN).putInt(v).array())
        fun i64(v: Long) = o.write(ByteBuffer.allocate(8).order(ByteOrder.LITTLE_ENDIAN).putLong(v).array())
        fun chaine(s: String) = s.toByteArray().let { i32(it.size); o.write(it) }
        i32(magie); chaine("const"); chaine("standard"); i32(2); i32(drapeaux); i64(0); i64(0); i64(1); i64(0)
        i32(2125658996); chaine("isyms"); i64(mots.size.toLong()); i64(mots.size.toLong())
        mots.forEachIndexed { k, m -> chaine(m); i64(k.toLong()) }
        File(dossier, "graph").mkdirs()
        File(dossier, "graph/Gr.fst").writeBytes(o.toByteArray())
    }

    @Test
    fun `seuls les candidats presents dans la table sont connus`() {
        ecrireFst(listOf("<eps>", "hetre", "chêne", "fox", "victor"))
        assertEquals(setOf("hetre", "fox"), VocabulaireVosk.motsConnus(dossier, setOf("hetre", "fox", "foxtrot")))
    }

    @Test
    fun `modele illisible ou sans table - tout est accepte`() {
        val candidats = setOf("hetre", "foxtrot")
        assertEquals(candidats, VocabulaireVosk.motsConnus(dossier, candidats)) // pas de Gr.fst
        ecrireFst(listOf("hetre"), drapeaux = 0)
        assertEquals(candidats, VocabulaireVosk.motsConnus(dossier, candidats))
        ecrireFst(listOf("hetre"), magie = 1)
        assertEquals(candidats, VocabulaireVosk.motsConnus(dossier, candidats))
    }
}
