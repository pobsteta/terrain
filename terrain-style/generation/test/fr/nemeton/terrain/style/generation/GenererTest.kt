package fr.nemeton.terrain.style.generation

import dynamiccolor.ColorSpec.SpecVersion
import dynamiccolor.DynamicScheme.Platform
import dynamiccolor.Variant
import fr.nemeton.terrain.style.Contraste
import fr.nemeton.terrain.style.Schema
import fr.nemeton.terrain.style.StyleTerrain
import hct.Hct
import palettes.TonalPalette
import scheme.SchemeContent
import java.io.File
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

/** Tests du script de génération (missions 006 et 007). Lancés par `./gradlew :terrain-style:testGeneration`. */
class GenererTest {

    private val module = File(System.getProperty("user.dir"))

    @Test
    fun `les deux schemas sont en SPEC_2021, CONTENT, contraste 1 et PHONE`() {
        assertEquals(Variant.CONTENT, Reglages.VARIANTE) // ADR 0004
        assertEquals(Platform.PHONE, Reglages.PLATEFORME)
        for (sombre in listOf(false, true)) {
            val s = schemaMcu(sombre)
            assertEquals(SpecVersion.SPEC_2021, s.specVersion)
            assertEquals(Variant.CONTENT, s.variant)
            assertEquals(1.0, s.contrastLevel)
            assertEquals(sombre, s.isDark)
            assertEquals(Reglages.PLATEFORME, s.platform)
        }
    }

    @Test
    fun `CONTENT sur PHONE impose SPEC_2021 meme si une autre version est demandee`() {
        for (v in SpecVersion.values()) {
            val s = SchemeContent(Hct.fromInt(Reglages.SOURCE.toInt()), false, 1.0, v, Platform.PHONE)
            assertEquals(SpecVersion.SPEC_2021, s.specVersion, "demandée : $v")
        }
    }

    @Test
    fun `CONTENT sur WATCH garderait la version demandee, d ou le reglage PHONE`() {
        val s = SchemeContent(Hct.fromInt(Reglages.SOURCE.toInt()), true, 1.0, SpecVersion.SPEC_2025, Platform.WATCH)
        assertEquals(SpecVersion.SPEC_2025, s.specVersion)
    }

    @Test
    fun `en sombre tertiary n est plus identique a error (ADR 0004)`() {
        for (sombre in listOf(false, true)) {
            val j = jetons(sombre)
            assertTrue(j.getValue("tertiary") != j.getValue("error"), "sombre=$sombre")
            assertTrue(j.getValue("tertiaryContainer") != j.getValue("errorContainer"), "sombre=$sombre")
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
        assertEquals(1 + 2 * (48 + 8), lignes.size)
        val schemas = mapOf("clair" to StyleTerrain.clair, "sombre" to StyleTerrain.sombre)
        fun lire(o: Any, champ: String) = o.javaClass.getMethod("get" + champ.replaceFirstChar { it.uppercase() }).invoke(o)
        for (l in lignes.drop(1)) {
            val (theme, role, argb) = l.split(',')
            // « reussi.couleur » : champ `couleur` de la `Semantique` `reussi`
            val valeur = role.split('.').fold(schemas.getValue(theme) as Any) { o, champ -> lire(o, champ) }
            assertEquals(java.lang.Long.parseLong(argb.removePrefix("0x"), 16), valeur, "$theme,$role")
        }
    }

    @Test
    fun `les interdits en texte de StyleTerrain sont ceux que calcule le script`() {
        assertEquals(interditsEnTexte(tousLesJetons(false)), StyleTerrain.clair.interditsEnTexte)
        assertEquals(interditsEnTexte(tousLesJetons(true)), StyleTerrain.sombre.interditsEnTexte)
    }

    // ---- Couleurs d'état (mission 007, ADR 0002) ----

    @Test
    fun `reussi a pour palette celle de 0F8558 telle quelle, sans harmonisation`() {
        assertEquals(0xFF0F8558L, Reglages.REUSSI)
        val vert = Hct.fromInt(Reglages.REUSSI.toInt())
        for (sombre in listOf(false, true)) {
            val p = schemaReussi(sombre).errorPalette
            assertEquals(vert.hue, p.hue, "sombre=$sombre")
            assertEquals(vert.chroma, p.chroma, "sombre=$sombre")
        }
    }

    @Test
    fun `le schema de reussi ne change que les roles error du schema du theme`() {
        val erreur = setOf("error", "onError", "errorContainer", "onErrorContainer")
        for (sombre in listOf(false, true)) {
            val base = jetons(sombre)
            val r = schemaReussi(sombre)
            val j = ROLES.associate { (n, f) -> n to versLong(f(r)) }
            assertEquals(base.filterKeys { it !in erreur }, j.filterKeys { it !in erreur }, "sombre=$sombre")
            assertTrue(j.getValue("error") != base.getValue("error"), "sombre=$sombre")
            assertTrue(j.getValue("errorContainer") != base.getValue("errorContainer"), "sombre=$sombre")
        }
    }

    @Test
    fun `le constructeur avec la palette d erreur par defaut redonne le schema du theme`() {
        // Le mécanisme lui-même : seule la palette passée change le résultat.
        for (sombre in listOf(false, true)) {
            val b = schemaMcu(sombre)
            val s = dynamiccolor.DynamicScheme(
                b.sourceColorHctList, b.variant, b.isDark, b.contrastLevel, b.platform, b.specVersion,
                b.primaryPalette, b.secondaryPalette, b.tertiaryPalette, b.neutralPalette, b.neutralVariantPalette,
                TonalPalette.fromHueAndChroma(25.0, 84.0), // palette d'erreur de ColorSpec2021.getErrorPalette
            )
            assertEquals(jetons(sombre), ROLES.associate { (n, f) -> n to versLong(f(s)) }, "sombre=$sombre")
        }
    }

    @Test
    fun `reussi vaut les roles error du schema de reussi`() {
        for (sombre in listOf(false, true)) {
            val r = schemaReussi(sombre)
            val e = semantiques(sombre)
            assertEquals(versLong(r.error), e.getValue("reussi.couleur"))
            assertEquals(versLong(r.onError), e.getValue("reussi.surCouleur"))
            assertEquals(versLong(r.errorContainer), e.getValue("reussi.conteneur"))
            assertEquals(versLong(r.onErrorContainer), e.getValue("reussi.surConteneur"))
        }
    }

    @Test
    fun `alerte est figee en E2AA00 et noir dans les deux themes`() {
        for (s in listOf(StyleTerrain.clair, StyleTerrain.sombre)) {
            assertEquals(0xFFE2AA00L, s.alerte.couleur)
            assertEquals(0xFFE2AA00L, s.alerte.conteneur)
            assertEquals(0xFF000000L, s.alerte.surCouleur)
            assertEquals(0xFF000000L, s.alerte.surConteneur)
        }
        // 9,98:1 (ADR 0002), tronqué
        assertEquals(9.983, tronque3(Contraste.rapport(Reglages.SUR_ALERTE, Reglages.ALERTE)))
    }

    @Test
    fun `les couples des couleurs d etat sont 12 et tiennent 7 pour 1, seul alerte couleur est interdit`() {
        val c = couplesSemantiques()
        assertEquals(12, c.size)
        assertEquals(c.size, c.toSet().size)
        assertTrue(("alerte.surCouleur" to "alerte.couleur") in c)
        for (sombre in listOf(false, true)) {
            val j = tousLesJetons(sombre)
            for ((t, f) in c) assertTrue(tronque3(Contraste.rapport(j.getValue(t), j.getValue(f))) >= Reglages.SEUIL_TEXTE, "$t / $f sombre=$sombre")
            assertEquals(setOf("alerte.couleur"), interditsEnTexte(j), "sombre=$sombre")
        }
    }

    @Test
    fun `l ecart de teinte HCT de reussi a primary est affiche en tete de StyleTerrain`() {
        val clair = degres(ecartReussiPrimary(false))
        val sombre = degres(ecartReussiPrimary(true))
        println("ECART reussi.couleur / primary (teinte HCT) : clair $clair°, sombre $sombre°")
        val entete = fichiers().getValue("src/main/kotlin/fr/nemeton/terrain/style/StyleTerrain.kt")
        assertTrue(entete.contains("clair $clair°, sombre $sombre°"), "écart absent de l'en-tête")
        for (sombreTheme in listOf(false, true)) assertTrue(ecartReussiPrimary(sombreTheme) in 0.0..180.0)
    }

    @Test
    fun `le nuancier montre erreur, reussi et alerte cote a cote dans les deux themes`() {
        val svg = nuancier(tousLesJetons(false), tousLesJetons(true))
        // un bandeau d'état par thème ; error et errorContainer ont aussi leur bandeau de rôle
        fun n(nom: String) = Regex(">${Regex.escape(nom)} #").findAll(svg).count()
        for (nom in listOf("reussi.couleur", "alerte.couleur", "reussi.conteneur", "alerte.conteneur")) assertEquals(2, n(nom), nom)
        for (nom in listOf("error", "errorContainer")) assertEquals(4, n(nom), nom)
        assertTrue(svg.contains(">alerte.couleur #E2AA00 (interdit en texte)<"))
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
