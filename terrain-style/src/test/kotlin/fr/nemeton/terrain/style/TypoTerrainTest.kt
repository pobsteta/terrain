package fr.nemeton.terrain.style

import org.junit.jupiter.api.DynamicTest
import org.junit.jupiter.api.TestFactory
import kotlin.test.Test
import kotlin.test.assertEquals

/**
 * Typographie de Marculus (mission 021, critère 2 de docs/spec.md, ADR 0006).
 *
 * Valeurs lues dans `docs/referentiels/style-marculus.md` § 3 (défauts Material 3 1.4.0 ;
 * Marculus ne fixe aucune typographie, écart E6), jamais dans le code. La famille de police
 * (SansSerif) n'est pas dans le contrat : le module reste Kotlin pur.
 *
 * Contrat d'API attendu (voir le corps de la PR) :
 * `TypoTerrain.Style(tailleSp: Int, interligneSp: Int, graisse: Int, interlettrageSp: Double)`
 * et 15 propriétés `TypoTerrain.displayLarge` … `TypoTerrain.labelSmall` de type `TypoTerrain.Style`.
 * `graisse` : poids CSS/Compose (400 Normal, 500 Medium). Égalités exactes, sans tolérance.
 */
class TypoTerrainTest {

    private val styles: List<Pair<String, TypoTerrain.Style>> = listOf(
        "displayLarge" to TypoTerrain.displayLarge,
        "displayMedium" to TypoTerrain.displayMedium,
        "displaySmall" to TypoTerrain.displaySmall,
        "headlineLarge" to TypoTerrain.headlineLarge,
        "headlineMedium" to TypoTerrain.headlineMedium,
        "headlineSmall" to TypoTerrain.headlineSmall,
        "titleLarge" to TypoTerrain.titleLarge,
        "titleMedium" to TypoTerrain.titleMedium,
        "titleSmall" to TypoTerrain.titleSmall,
        "bodyLarge" to TypoTerrain.bodyLarge,
        "bodyMedium" to TypoTerrain.bodyMedium,
        "bodySmall" to TypoTerrain.bodySmall,
        "labelLarge" to TypoTerrain.labelLarge,
        "labelMedium" to TypoTerrain.labelMedium,
        "labelSmall" to TypoTerrain.labelSmall,
    )

    @Test
    fun `le referentiel compte 15 styles et les 15 noms de Typography`() {
        assertEquals(15, ReferentielMarculus.typographie().size)
        assertEquals(styles.map { it.first }.toSet(), ReferentielMarculus.typographie().keys)
        assertEquals(15, styles.size)
    }

    @TestFactory
    fun `chaque style est egal au referentiel, taille, interligne, graisse, interlettrage`(): List<DynamicTest> {
        val ref = ReferentielMarculus.typographie()
        return styles.flatMap { (nom, s) ->
            val a = ref[nom] ?: error("style-marculus.md § 3 : style $nom absent")
            val l = "style-marculus.md L${a.numero}"
            listOf(
                DynamicTest.dynamicTest("$nom : taille") { assertEquals(a.valeur.taille, s.tailleSp, "$nom taille en sp ($l)") },
                DynamicTest.dynamicTest("$nom : interligne") { assertEquals(a.valeur.interligne, s.interligneSp, "$nom interligne en sp ($l)") },
                DynamicTest.dynamicTest("$nom : graisse") { assertEquals(a.valeur.graisse, s.graisse, "$nom graisse ($l)") },
                DynamicTest.dynamicTest("$nom : interlettrage") { assertEquals(a.valeur.interlettrage, s.interlettrageSp, "$nom interlettrage en sp ($l)") },
            )
        }
    }

    @Test
    fun `trois styles lus en clair, pour l oeil du relecteur`() {
        // style-marculus.md § 3 : displayLarge 57/64/400/-0,2 ; bodySmall 12/16/400/0,4 ; labelSmall 11/16/500/0,5.
        TypoTerrain.displayLarge.let {
            assertEquals(57, it.tailleSp); assertEquals(64, it.interligneSp); assertEquals(400, it.graisse); assertEquals(-0.2, it.interlettrageSp)
        }
        TypoTerrain.bodySmall.let {
            assertEquals(12, it.tailleSp); assertEquals(16, it.interligneSp); assertEquals(400, it.graisse); assertEquals(0.4, it.interlettrageSp)
        }
        TypoTerrain.labelSmall.let {
            assertEquals(11, it.tailleSp); assertEquals(16, it.interligneSp); assertEquals(500, it.graisse); assertEquals(0.5, it.interlettrageSp)
        }
    }

    @Test
    fun `TypoTerrain n expose que les 15 styles (les Emphasized internal ne sont pas relevees)`() {
        val styleClass = TypoTerrain.Style::class.java
        val getters = TypoTerrain::class.java.methods
            .filter { it.parameterCount == 0 && it.returnType == styleClass && it.name.startsWith("get") }
            .map { it.name.removePrefix("get").replaceFirstChar { c -> c.lowercase() } }
            .toSet()
        assertEquals(styles.map { it.first }.toSet(), getters, "propriétés de type TypoTerrain.Style")
    }
}
