package fr.nemeton.terrain.style

import org.junit.jupiter.api.DynamicTest
import org.junit.jupiter.api.TestFactory
import java.io.File
import java.util.Locale
import kotlin.math.floor
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

/**
 * Tests d'acceptation des jetons (mission 005, critères 2 et 5 de docs/spec.md).
 *
 * Écrits sans lire le code de génération : seule l'API de la spec est connue
 * (`StyleTerrain.clair/sombre`, `Schema`, `Semantique`, `Contraste.rapport`).
 *
 * Révisé par la mission 021 (ADR 0006, 2026-10-24) : la règle 7:1 tombe comme règle commune.
 * Le test des couples est devenu informatif (il affiche le rapport, sans seuil) ;
 * `interditsEnTexte` vaut `{alerte.couleur}`. L'égalité des valeurs au style de Marculus est
 * dans `StyleMarculusTest` (couleurs), `FormesTerrainTest`, `TypoTerrainTest`, `BoutonsTerrainTest`.
 * Restent ici : la forme du schéma, la plage ARGB, les refus d'`IllegalArgumentException`.
 *
 * Convention : le nom d'un rôle interdit en texte est le nom du champ (`primary`) ou, pour une
 * couleur sémantique, `reussi.couleur`, `alerte.couleur`, etc. Rapport tronqué à 3 décimales
 * (jamais arrondi) pour l'affichage.
 */
class JetonsTest {

    // ---- Les 48 rôles de ColorScheme (Compose Material 3 1.4.0, docs/referentiels/mcu-compose.md §2.2) ----

    private val roles: Map<String, (Schema) -> Long> = linkedMapOf(
        "primary" to { s -> s.primary }, "onPrimary" to { s -> s.onPrimary },
        "primaryContainer" to { s -> s.primaryContainer }, "onPrimaryContainer" to { s -> s.onPrimaryContainer },
        "inversePrimary" to { s -> s.inversePrimary },
        "secondary" to { s -> s.secondary }, "onSecondary" to { s -> s.onSecondary },
        "secondaryContainer" to { s -> s.secondaryContainer }, "onSecondaryContainer" to { s -> s.onSecondaryContainer },
        "tertiary" to { s -> s.tertiary }, "onTertiary" to { s -> s.onTertiary },
        "tertiaryContainer" to { s -> s.tertiaryContainer }, "onTertiaryContainer" to { s -> s.onTertiaryContainer },
        "background" to { s -> s.background }, "onBackground" to { s -> s.onBackground },
        "surface" to { s -> s.surface }, "onSurface" to { s -> s.onSurface },
        "surfaceVariant" to { s -> s.surfaceVariant }, "onSurfaceVariant" to { s -> s.onSurfaceVariant },
        "surfaceTint" to { s -> s.surfaceTint },
        "inverseSurface" to { s -> s.inverseSurface }, "inverseOnSurface" to { s -> s.inverseOnSurface },
        "error" to { s -> s.error }, "onError" to { s -> s.onError },
        "errorContainer" to { s -> s.errorContainer }, "onErrorContainer" to { s -> s.onErrorContainer },
        "outline" to { s -> s.outline }, "outlineVariant" to { s -> s.outlineVariant }, "scrim" to { s -> s.scrim },
        "surfaceBright" to { s -> s.surfaceBright }, "surfaceDim" to { s -> s.surfaceDim },
        "surfaceContainer" to { s -> s.surfaceContainer }, "surfaceContainerHigh" to { s -> s.surfaceContainerHigh },
        "surfaceContainerHighest" to { s -> s.surfaceContainerHighest },
        "surfaceContainerLow" to { s -> s.surfaceContainerLow }, "surfaceContainerLowest" to { s -> s.surfaceContainerLowest },
        "primaryFixed" to { s -> s.primaryFixed }, "primaryFixedDim" to { s -> s.primaryFixedDim },
        "onPrimaryFixed" to { s -> s.onPrimaryFixed }, "onPrimaryFixedVariant" to { s -> s.onPrimaryFixedVariant },
        "secondaryFixed" to { s -> s.secondaryFixed }, "secondaryFixedDim" to { s -> s.secondaryFixedDim },
        "onSecondaryFixed" to { s -> s.onSecondaryFixed }, "onSecondaryFixedVariant" to { s -> s.onSecondaryFixedVariant },
        "tertiaryFixed" to { s -> s.tertiaryFixed }, "tertiaryFixedDim" to { s -> s.tertiaryFixedDim },
        "onTertiaryFixed" to { s -> s.onTertiaryFixed }, "onTertiaryFixedVariant" to { s -> s.onTertiaryFixedVariant },
    )

    private val semantiques: Map<String, (Schema) -> Semantique> =
        linkedMapOf("reussi" to { s -> s.reussi }, "alerte" to { s -> s.alerte })

    /** Nom -> valeur pour les 48 rôles et les 8 rôles sémantiques (`reussi.couleur`…). */
    private fun jetons(s: Schema): Map<String, Long> {
        val m = LinkedHashMap<String, Long>()
        roles.forEach { (nom, f) -> m[nom] = f(s) }
        semantiques.forEach { (nom, f) ->
            val x = f(s)
            m["$nom.couleur"] = x.couleur
            m["$nom.surCouleur"] = x.surCouleur
            m["$nom.conteneur"] = x.conteneur
            m["$nom.surConteneur"] = x.surConteneur
        }
        return m
    }

    private val themes: List<Pair<String, Schema>> get() = listOf("clair" to StyleTerrain.clair, "sombre" to StyleTerrain.sombre)

    // ---- Les couples du critère 2 (la spec les fixe) ----

    /** (texte, fond) par nom de jeton. */
    private class Couple(val texte: String, val fond: String) {
        override fun equals(other: Any?) = other is Couple && other.texte == texte && other.fond == fond
        override fun hashCode() = texte.hashCode() * 31 + fond.hashCode()
        override fun toString() = "$texte / $fond"
    }

    private val surfaces = listOf(
        "surface", "surfaceDim", "surfaceBright", "surfaceContainer", "surfaceContainerHigh",
        "surfaceContainerHighest", "surfaceContainerLow", "surfaceContainerLowest",
    )

    private fun couples(): List<Couple> {
        val l = LinkedHashSet<Couple>()
        // 1. chaque onX sur son fond X
        for (x in listOf("primary", "primaryContainer", "secondary", "secondaryContainer", "tertiary",
            "tertiaryContainer", "error", "errorContainer", "background", "surface", "surfaceVariant")) {
            l += Couple("on" + x.replaceFirstChar { it.uppercase() }, x)
        }
        l += Couple("inverseOnSurface", "inverseSurface")
        // 2. onXFixed et onXFixedVariant sur XFixed et XFixedDim
        for (x in listOf("primary", "secondary", "tertiary")) {
            val on = "on" + x.replaceFirstChar { it.uppercase() }
            for (t in listOf("${on}Fixed", "${on}FixedVariant")) for (f in listOf("${x}Fixed", "${x}FixedDim")) l += Couple(t, f)
        }
        // 3. onSurface et onSurfaceVariant sur les huit surfaces
        for (t in listOf("onSurface", "onSurfaceVariant")) for (f in surfaces) l += Couple(t, f)
        // 4. primary, secondary, tertiary, error, reussi.couleur en texte sur les huit surfaces
        for (t in listOf("primary", "secondary", "tertiary", "error", "reussi.couleur")) for (f in surfaces) l += Couple(t, f)
        // 5. inversePrimary sur inverseSurface
        l += Couple("inversePrimary", "inverseSurface")
        // 6. couleurs sémantiques
        for (x in listOf("reussi", "alerte")) {
            l += Couple("$x.surCouleur", "$x.couleur")
            l += Couple("$x.surConteneur", "$x.conteneur")
        }
        return l.toList()
    }

    // ---- Tronquer, jamais arrondir ----

    /**
     * Tronque à 3 décimales par le bas, jamais d'arrondi (la spec : 4,499:1 ne tient pas 4,5:1).
     * La tolérance de 1e-9 avant `floor` ne sert pas à arrondir : elle absorbe le bruit flottant
     * (un rapport exact de 7,000 calculé en 6,999999999999), comme dans `ContrasteTest`. Elle est
     * bien plus petite que la troncature visée (1e-3), donc 6,9999999 reste 6,999.
     */
    private fun tronque3(r: Double): Double = floor(r * 1000.0 + 1e-9) / 1000.0

    private fun fmt(d: Double) = String.format(Locale.ROOT, "%.3f", d)

    private fun hex(v: Long) = "0x" + java.lang.Long.toHexString(v).uppercase().padStart(8, '0')

    @Test
    fun `la troncature n arrondit jamais`() {
        assertEquals(6.999, tronque3(6.9999999))
        assertEquals(7.0, tronque3(7.0))
        assertEquals(7.0, tronque3(7.0004))
        assertEquals(4.499, tronque3(4.4999))
    }

    @Test
    fun `la liste des couples de la spec compte 84 couples distincts par theme`() {
        // 12 (onX/X) ; 12 (fixes) ; 16 (onSurface*, 8 surfaces) - 1 doublon onSurface/surface déjà en 1 ;
        // 40 (texte coloré sur 8 surfaces) ; 1 (inversePrimary) ; 4 (sémantiques) = 84
        val c = couples()
        assertEquals(84, c.size, "couples distincts : $c")
        assertEquals(c.size, c.map { it.toString() }.toSet().size)
    }

    // ---- Contraste, pour information (ADR 0006 : la règle 7:1 tombe comme règle commune) ----

    /**
     * Affiche le rapport de chaque couple de la liste du critère 2, dans les deux thèmes, SANS
     * échouer sur la valeur : le style est celui de Marculus, il n'a pas à tenir 7:1. Le test
     * échoue seulement si un rapport n'est pas un nombre fini compris entre 1 et 21 (ce qui
     * dirait que `Contraste` est cassé), ou si un jeton du couple est inconnu.
     */
    @TestFactory
    fun `le rapport de chaque couple est affiche pour information, sans seuil`(): List<DynamicTest> =
        themes.flatMap { (theme, schema) ->
            val valeurs = jetons(schema)
            couples().map { c ->
                DynamicTest.dynamicTest("$theme : ${c.texte} sur ${c.fond}") {
                    val t = valeurs[c.texte] ?: error("jeton inconnu ${c.texte}")
                    val f = valeurs[c.fond] ?: error("jeton inconnu ${c.fond}")
                    val brut = Contraste.rapport(t, f)
                    println(
                        "RAPPORT %-6s %-26s sur %-24s %s/%s = %s:1".format(
                            theme, c.texte, c.fond, hex(t), hex(f), fmt(tronque3(brut)),
                        ),
                    )
                    assertTrue(brut.isFinite() && brut >= 1.0, "$theme : ${c.texte} sur ${c.fond} : rapport $brut < 1")
                    assertTrue(brut <= 21.0 + 1e-9, "$theme : ${c.texte} sur ${c.fond} : rapport $brut > 21")
                }
            }
        }

    // ---- Rôles interdits en texte ----

    @Test
    fun `interditsEnTexte vaut exactement alerte couleur dans les deux themes`() {
        for ((theme, s) in themes) assertEquals(setOf("alerte.couleur"), s.interditsEnTexte, "$theme : interditsEnTexte")
    }

    @Test
    fun `interditsEnTexte ne contient que des noms de jetons connus`() {
        for ((theme, s) in themes) {
            val connus = jetons(s).keys
            for (n in s.interditsEnTexte) assertTrue(n in connus, "$theme : '$n' n'est pas un jeton connu")
        }
    }

    @Test
    fun `chaque role interdit en texte est documente dans le README`() {
        val readme = listOf("README.md", "../README.md").map(::File).firstOrNull { it.isFile }
            ?: error("README.md introuvable depuis ${File(".").absolutePath}")
        val texte = readme.readText()
        for ((theme, s) in themes) for (n in s.interditsEnTexte) {
            // nom exact entre accents graves : `primary` ne doit pas être couvert par `primaryContainer`
            assertTrue(texte.contains("`$n`"), "$theme : le rôle interdit en texte '$n' n'est pas documenté dans README.md (attendu : `$n`)")
        }
    }

    // ---- Le schéma est complet (48 rôles + 2 sémantiques) ----

    @Test
    fun `le schema porte exactement les 48 roles de ColorScheme sans doublon`() {
        assertEquals(48, roles.size)
        val champs = Schema::class.java.methods
            .filter { it.parameterCount == 0 && it.returnType == java.lang.Long.TYPE && it.name.startsWith("get") }
            .map { it.name.removePrefix("get").replaceFirstChar { c -> c.lowercase() } }
            .toSet()
        assertEquals(roles.keys, champs, "champs Long de Schema différents des 48 rôles de la spec")
    }

    @Test
    fun `les deux themes sont distincts`() {
        assertTrue(jetons(StyleTerrain.clair) != jetons(StyleTerrain.sombre))
        assertTrue(StyleTerrain.clair.background != StyleTerrain.sombre.background)
    }

    // ---- Critère 5 : plage ARGB ----

    @Test
    fun `chaque jeton est dans 0xFF000000 a 0xFFFFFFFF avec alpha FF et sans extension de signe`() {
        for ((theme, s) in themes) for ((nom, v) in jetons(s)) {
            assertTrue(v in 0xFF000000L..0xFFFFFFFFL, "$theme : $nom = $v (${java.lang.Long.toHexString(v)}) hors de [0xFF000000, 0xFFFFFFFF]")
            assertEquals(0xFFL, (v ushr 24) and 0xFFL, "$theme : $nom alpha != FF")
            assertEquals(0L, v ushr 32, "$theme : $nom a des bits au-dessus de 32")
        }
    }

    @Test
    fun `Semantique refuse toute valeur hors plage ARGB par IllegalArgumentException`() {
        val ok = 0xFF123456L
        val mauvaises = listOf(
            0x00123456L, 0x80123456L, 0xFE123456L, 0x123456L, 0L, -1L,
            0xFF000000L - 1, 0x1_FF123456L, Long.MAX_VALUE, Long.MIN_VALUE,
        )
        for (m in mauvaises) {
            val h = java.lang.Long.toHexString(m)
            assertFailsWith<IllegalArgumentException>("couleur $h") { Semantique(m, ok, ok, ok) }
            assertFailsWith<IllegalArgumentException>("surCouleur $h") { Semantique(ok, m, ok, ok) }
            assertFailsWith<IllegalArgumentException>("conteneur $h") { Semantique(ok, ok, m, ok) }
            assertFailsWith<IllegalArgumentException>("surConteneur $h") { Semantique(ok, ok, ok, m) }
        }
    }

    @Test
    fun `Semantique accepte les bornes de la plage`() {
        val s = Semantique(0xFF000000L, 0xFFFFFFFFL, 0xFF000000L, 0xFFFFFFFFL)
        assertEquals(0xFF000000L, s.couleur)
        assertEquals(0xFFFFFFFFL, s.surCouleur)
    }

    /** Construit un Schema valide, sauf le rôle `cible` qui reçoit `valeur`. */
    private fun schemaAvec(cible: String, valeur: Long): Schema {
        val ok = 0xFF123456L
        fun v(nom: String) = if (nom == cible) valeur else ok
        val sem = Semantique(ok, ok, ok, ok)
        return Schema(
            primary = v("primary"),
            onPrimary = v("onPrimary"),
            primaryContainer = v("primaryContainer"),
            onPrimaryContainer = v("onPrimaryContainer"),
            inversePrimary = v("inversePrimary"),
            secondary = v("secondary"),
            onSecondary = v("onSecondary"),
            secondaryContainer = v("secondaryContainer"),
            onSecondaryContainer = v("onSecondaryContainer"),
            tertiary = v("tertiary"),
            onTertiary = v("onTertiary"),
            tertiaryContainer = v("tertiaryContainer"),
            onTertiaryContainer = v("onTertiaryContainer"),
            background = v("background"),
            onBackground = v("onBackground"),
            surface = v("surface"),
            onSurface = v("onSurface"),
            surfaceVariant = v("surfaceVariant"),
            onSurfaceVariant = v("onSurfaceVariant"),
            surfaceTint = v("surfaceTint"),
            inverseSurface = v("inverseSurface"),
            inverseOnSurface = v("inverseOnSurface"),
            error = v("error"),
            onError = v("onError"),
            errorContainer = v("errorContainer"),
            onErrorContainer = v("onErrorContainer"),
            outline = v("outline"),
            outlineVariant = v("outlineVariant"),
            scrim = v("scrim"),
            surfaceBright = v("surfaceBright"),
            surfaceDim = v("surfaceDim"),
            surfaceContainer = v("surfaceContainer"),
            surfaceContainerHigh = v("surfaceContainerHigh"),
            surfaceContainerHighest = v("surfaceContainerHighest"),
            surfaceContainerLow = v("surfaceContainerLow"),
            surfaceContainerLowest = v("surfaceContainerLowest"),
            primaryFixed = v("primaryFixed"),
            primaryFixedDim = v("primaryFixedDim"),
            onPrimaryFixed = v("onPrimaryFixed"),
            onPrimaryFixedVariant = v("onPrimaryFixedVariant"),
            secondaryFixed = v("secondaryFixed"),
            secondaryFixedDim = v("secondaryFixedDim"),
            onSecondaryFixed = v("onSecondaryFixed"),
            onSecondaryFixedVariant = v("onSecondaryFixedVariant"),
            tertiaryFixed = v("tertiaryFixed"),
            tertiaryFixedDim = v("tertiaryFixedDim"),
            onTertiaryFixed = v("onTertiaryFixed"),
            onTertiaryFixedVariant = v("onTertiaryFixedVariant"),
            reussi = sem,
            alerte = sem,
            interditsEnTexte = emptySet(),
        )
    }

    @Test
    fun `Schema accepte une valeur valide pour chacun de ses 48 roles`() {
        for (nom in roles.keys) {
            val s = schemaAvec(nom, 0xFF654321L)
            assertEquals(0xFF654321L, roles.getValue(nom)(s), nom)
        }
    }

    @Test
    fun `Schema refuse toute valeur hors plage ARGB, sur chacun de ses 48 roles, par IllegalArgumentException`() {
        val mauvaises = listOf(0x00123456L, 0x80123456L, 0x123456L, 0L, -1L, 0x1_FF123456L, Long.MAX_VALUE, Long.MIN_VALUE)
        for (nom in roles.keys) for (m in mauvaises) {
            assertFailsWith<IllegalArgumentException>("$nom = ${java.lang.Long.toHexString(m)}") { schemaAvec(nom, m) }
        }
    }
}
