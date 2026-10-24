package fr.nemeton.terrain.voix

import java.io.ByteArrayOutputStream
import java.io.File
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.file.Files
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

class VocabulaireVoskTest {
    private val dossier: File = Files.createTempDirectory("modele").toFile()

    /** En-tête OpenFst minimal suivi de la table des symboles d'entrée. */
    private fun ecrireFst(mots: List<String>, drapeaux: Int = 1, magie: Int = 2125659606, sortie: List<String>? = null) {
        val o = ByteArrayOutputStream()
        fun i32(v: Int) = o.write(ByteBuffer.allocate(4).order(ByteOrder.LITTLE_ENDIAN).putInt(v).array())
        fun i64(v: Long) = o.write(ByteBuffer.allocate(8).order(ByteOrder.LITTLE_ENDIAN).putLong(v).array())
        fun chaine(s: String) = s.toByteArray().let { i32(it.size); o.write(it) }
        i32(magie); chaine("const"); chaine("standard"); i32(2); i32(drapeaux); i64(0); i64(0); i64(1); i64(0)
        i32(2125658996); chaine("isyms"); i64(mots.size.toLong()); i64(mots.size.toLong())
        mots.forEachIndexed { k, m -> chaine(m); i64(k.toLong()) }
        if (sortie != null) {
            i32(2125658996); chaine("osyms"); i64(sortie.size.toLong()); i64(sortie.size.toLong())
            sortie.forEachIndexed { k, m -> chaine(m); i64(k.toLong()) }
        }
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

    // ---- Mission 017, critère 7 : lireVocabulaire (Connus / Illisible), sans repli sur words.txt.
    // Hypothèse de nom : Connus.mots et Illisible.raison (spec : « Connus(mots) », « Illisible(raison) »).

    @Test
    fun `critere 7 - lireVocabulaire - le jeu actuel donne Connus, seuls les candidats presents`() {
        ecrireFst(listOf("<eps>", "hetre", "chêne", "fox", "victor"))
        val r = VocabulaireVosk.lireVocabulaire(dossier, setOf("hetre", "fox", "foxtrot"))
        assertEquals(setOf("hetre", "fox"), assertIs<Connus>(r).mots)
    }

    @Test
    fun `critere 7 - lireVocabulaire - Gr-fst absent donne Illisible(pas de Gr-fst)`() {
        val r = VocabulaireVosk.lireVocabulaire(dossier, setOf("hetre", "foxtrot"))
        assertEquals("pas de Gr.fst", assertIs<Illisible>(r).raison)
    }

    @Test
    fun `critere 7 - lireVocabulaire - Gr-fst absent mais words-txt present - toujours Illisible, pas de repli`() {
        File(dossier, "graph").mkdirs()
        File(dossier, "graph/words.txt").writeText("hetre 1\nfox 2\n")
        assertIs<Illisible>(VocabulaireVosk.lireVocabulaire(dossier, setOf("hetre", "fox")))
    }

    @Test
    fun `critere 7 - lireVocabulaire - Gr-fst vide (0 octet) donne Illisible`() {
        File(dossier, "graph").mkdirs()
        File(dossier, "graph/Gr.fst").writeBytes(ByteArray(0))
        assertIs<Illisible>(VocabulaireVosk.lireVocabulaire(dossier, setOf("hetre")))
    }

    @Test
    fun `critere 7 - lireVocabulaire - en-tete sans table de symboles donne Illisible`() {
        ecrireFst(listOf("hetre"), drapeaux = 0)
        assertIs<Illisible>(VocabulaireVosk.lireVocabulaire(dossier, setOf("hetre")))
    }

    @Test
    fun `critere 7 - lireVocabulaire - mauvais nombre magique donne Illisible`() {
        ecrireFst(listOf("hetre"), magie = 1)
        assertIs<Illisible>(VocabulaireVosk.lireVocabulaire(dossier, setOf("hetre")))
    }

    @Test
    fun `critere 7 - lireVocabulaire - lit la table de SORTIE, pas celle d'entree`() {
        // Décision de Pascal (2026-10-24) : on lit la table de sortie si elle existe, sinon celle
        // d'entrée. Sur vosk-model-small-fr-0.22, les deux tables sont identiques (135 774 symboles,
        // mission 016) : le jeu d'essai les distingue exprès, pour savoir laquelle est lue.
        ecrireFst(mots = listOf("<eps>", "entree_seule"), drapeaux = 3, sortie = listOf("<eps>", "hetre", "fox"))
        val r = VocabulaireVosk.lireVocabulaire(dossier, setOf("hetre", "fox", "entree_seule"))
        assertEquals(setOf("hetre", "fox"), assertIs<Connus>(r).mots)
    }

    @Test
    fun `critere 7 - motsConnus est inchange - Gr-fst absent, tout est accepte`() {
        val candidats = setOf("hetre", "foxtrot")
        assertEquals(candidats, VocabulaireVosk.motsConnus(dossier, candidats))
    }
}
