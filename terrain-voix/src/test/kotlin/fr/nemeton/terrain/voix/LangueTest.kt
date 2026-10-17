package fr.nemeton.terrain.voix

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class LangueTest {

    @Test
    fun `nombres - unites, dizaines, soixante-dix et quatre-vingts, centaines`() {
        assertEquals(listOf("cinq"), NombresFrancais.jetons(5))
        assertEquals(listOf("quarante", "cinq"), NombresFrancais.jetons(45))
        assertEquals(listOf("vingt", "et", "un"), NombresFrancais.jetons(21))
        assertEquals(listOf("soixante", "et", "onze"), NombresFrancais.jetons(71))
        assertEquals(listOf("soixante", "quinze"), NombresFrancais.jetons(75))
        assertEquals(listOf("quatre", "vingt"), NombresFrancais.jetons(80))
        assertEquals(listOf("quatre", "vingt", "dix"), NombresFrancais.jetons(90))
        assertEquals(listOf("cent"), NombresFrancais.jetons(100))
        assertEquals(listOf("deux", "cent", "quarante"), NombresFrancais.jetons(240))
        assertFailsWith<IllegalArgumentException> { NombresFrancais.jetons(0) }
        assertFailsWith<IllegalArgumentException> { NombresFrancais.jetons(1000) }
    }

    @Test
    fun `alphabet radio - replis quand le modele ignore le mot OTAN`() {
        val connu: (String) -> Boolean = { it !in setOf("foxtrot", "juliett", "uniform", "xray") }
        assertEquals("foxtrot", AlphabetRadio.mot('F'))
        assertEquals("fox", AlphabetRadio.mot('f', connu))
        assertEquals("juliette", AlphabetRadio.mot('J', connu))
        assertEquals("xavier", AlphabetRadio.mot('X', connu))
        assertEquals("alpha", AlphabetRadio.mot('A', connu))
        assertEquals("unite", AlphabetRadio.mot('7'))
        assertEquals(26, AlphabetRadio.CANONIQUE.size)
        assertTrue("uniforme" in AlphabetRadio.TOUS_LES_MOTS)
    }

    @Test
    fun `grammaire fermee - doublons, guillemets, inconnu en dernier`() {
        assertEquals("""["hetre", "chene sessile", "[unk]"]""", GrammaireFermee.json(listOf("hetre", "chene sessile", "hetre", "", "[unk]")))
        assertEquals("""["dit x", "[unk]"]""", GrammaireFermee.json(listOf("dit \"x\"")))
        assertEquals(listOf("hetre", "quarante"), GrammaireFermee.jetons(" hetre [unk] quarante "))
        assertTrue(GrammaireFermee.contientInconnu("hetre [unk]"))
        assertFalse(GrammaireFermee.contientInconnu("hetre quarante"))
    }
}
