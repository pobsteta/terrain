package fr.nemeton.terrain.style.generation

import dynamiccolor.ColorSpec.SpecVersion
import fr.nemeton.terrain.style.Schema
import fr.nemeton.terrain.style.StyleTerrain
import hct.Hct
import scheme.SchemeFidelity
import java.io.File
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

/** Tests du script de génération (mission 006). Lancés par `./gradlew :terrain-style:testGeneration`. */
class GenererTest {

    private val module = File(System.getProperty("user.dir"))

    @Test
    fun `les deux schemas sont en SPEC_2021, FIDELITY, contraste 1 et PHONE`() {
        for (sombre in listOf(false, true)) {
            val s = schemaMcu(sombre)
            assertEquals(SpecVersion.SPEC_2021, s.specVersion)
            assertEquals(Reglages.VARIANTE, s.variant)
            assertEquals(1.0, s.contrastLevel)
            assertEquals(sombre, s.isDark)
            assertEquals(Reglages.PLATEFORME, s.platform)
        }
    }

    @Test
    fun `FIDELITY impose SPEC_2021 meme si une autre version est demandee`() {
        for (v in SpecVersion.values()) {
            val s = SchemeFidelity(Hct.fromInt(Reglages.SOURCE.toInt()), false, 1.0, v, Reglages.PLATEFORME)
            assertEquals(SpecVersion.SPEC_2021, s.specVersion, "demandée : $v")
        }
    }

    @Test
    fun `la couleur de depart relue en HCT redonne 374742`() {
        assertEquals(0xFF374742L, Reglages.SOURCE)
        assertEquals(Reglages.SOURCE, versLong(Hct.fromInt(Reglages.SOURCE.toInt()).toInt()))
    }

    @Test
    fun `versLong ne propage pas le signe de l Int de MCU`() {
        assertEquals(0xFF000000L, versLong(0xFF000000.toInt()))
        assertEquals(0xFFFFFFFFL, versLong(-1))
        assertEquals(0xFF374742L, versLong(0xFF374742.toInt()))
    }

    @Test
    fun `48 roles par theme, aux noms des champs Long de Schema`() {
        val noms = ROLES.map { it.first }
        assertEquals(48, noms.size)
        assertEquals(48, noms.toSet().size)
        val champs = Schema::class.java.methods
            .filter { it.parameterCount == 0 && it.returnType == java.lang.Long.TYPE && it.name.startsWith("get") }
            .map { it.name.removePrefix("get").replaceFirstChar { c -> c.lowercase() } }
            .toSet()
        assertEquals(noms.toSet(), champs)
        for (sombre in listOf(false, true)) assertEquals(noms, jetons(sombre).keys.toList())
    }

    @Test
    fun `deux executions donnent les memes octets`() {
        assertEquals(fichiers(), fichiers())
    }

    @Test
    fun `les fichiers commites sont ceux que le script produit`() {
        for ((chemin, contenu) in fichiers()) {
            val f = File(module, chemin)
            assertTrue(f.isFile, "$chemin absent : lancer ./gradlew :terrain-style:generer")
            assertEquals(contenu, f.readText(Charsets.UTF_8), "$chemin diffère : lancer ./gradlew :terrain-style:generer")
        }
    }

    @Test
    fun `la table des jetons relue est StyleTerrain`() {
        val lignes = File(module, "src/test/resources/jetons/jetons.csv").readLines()
        assertEquals("theme,role,argb", lignes.first())
        assertEquals(1 + 2 * 48, lignes.size)
        val schemas = mapOf("clair" to StyleTerrain.clair, "sombre" to StyleTerrain.sombre)
        for (l in lignes.drop(1)) {
            val (theme, role, argb) = l.split(',')
            val getter = Schema::class.java.getMethod("get" + role.replaceFirstChar { it.uppercase() })
            assertEquals(java.lang.Long.parseLong(argb.removePrefix("0x"), 16), getter.invoke(schemas.getValue(theme)), "$theme,$role")
        }
    }

    @Test
    fun `les interdits en texte de StyleTerrain sont ceux que calcule le script`() {
        assertEquals(interditsEnTexte(jetons(false)), StyleTerrain.clair.interditsEnTexte)
        assertEquals(interditsEnTexte(jetons(true)), StyleTerrain.sombre.interditsEnTexte)
    }

    @Test
    fun `les couples sont ceux du critere 2 sans les couleurs semantiques, 72 distincts`() {
        // 84 couples de la spec - 4 sémantiques - 8 de reussi.couleur sur les surfaces = 72
        val c = couples()
        assertEquals(72, c.size)
        assertEquals(c.size, c.toSet().size)
        val roles = ROLES.map { it.first }.toSet()
        for ((t, f) in c) assertTrue(t in roles && f in roles, "$t / $f")
        assertTrue("onPrimary" to "primary" in c)
        assertTrue("inversePrimary" to "inverseSurface" in c)
        assertTrue("onTertiaryFixedVariant" to "tertiaryFixedDim" in c)
    }

    @Test
    fun `un role texte sous 7 sur un seul de ses fonds est interdit, sans retouche`() {
        val j = LinkedHashMap(jetons(false))
        val avant = HashMap(j)
        j["surfaceDim"] = j.getValue("primary") // primary sur surfaceDim : 1:1, ses autres couples tiennent
        val i = interditsEnTexte(j)
        assertTrue("primary" in i)
        assertFalse("surfaceDim" in i, "un fond n'est pas interdit en texte parce que le texte posé dessus échoue")
        assertFalse("onPrimary" in i)
        assertEquals(avant.filterKeys { it != "surfaceDim" }, j.filterKeys { it != "surfaceDim" })
        assertEquals(emptySet(), interditsEnTexte(avant))
    }

    @Test
    fun `la troncature n arrondit jamais`() {
        assertEquals(6.999, tronque3(6.9999999))
        assertEquals(7.0, tronque3(7.0))
        assertEquals(4.499, tronque3(4.4999))
    }
}
