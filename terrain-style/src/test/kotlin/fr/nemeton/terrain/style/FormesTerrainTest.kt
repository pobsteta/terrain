package fr.nemeton.terrain.style

import org.junit.jupiter.api.DynamicTest
import org.junit.jupiter.api.TestFactory
import kotlin.test.Test
import kotlin.test.assertEquals

/**
 * Formes de Marculus (mission 021, critère 2 de docs/spec.md, ADR 0006).
 *
 * Valeurs lues dans `docs/referentiels/style-marculus.md` § 2 (défauts Material 3 1.4.0 ;
 * Marculus ne fixe aucune forme au niveau du thème, écart E6), jamais dans le code.
 *
 * Contrat d'API attendu (types : `Int`, rayons en dp ; voir le corps de la PR) :
 * `FormesTerrain.extraSmall`, `small`, `medium`, `large`, `extraLarge` : `Float` ;
 * `FormesTerrain.pilulePourcent` : `Int` (écart E4 : la pilule est `RoundedCornerShape(50)`).
 */
class FormesTerrainTest {

    private val formes: List<Pair<String, Float>> = listOf(
        "extraSmall" to FormesTerrain.extraSmall,
        "small" to FormesTerrain.small,
        "medium" to FormesTerrain.medium,
        "large" to FormesTerrain.large,
        "extraLarge" to FormesTerrain.extraLarge,
    )

    @Test
    fun `le referentiel compte 5 formes publiques`() {
        assertEquals(
            listOf("extraSmall", "small", "medium", "large", "extraLarge"),
            ReferentielMarculus.formes().keys.toList(),
        )
    }

    @TestFactory
    fun `chaque rayon est egal au referentiel, en dp`(): List<DynamicTest> {
        val ref = ReferentielMarculus.formes()
        return formes.map { (nom, valeur) ->
            DynamicTest.dynamicTest(nom) {
                val attendu = ref[nom] ?: error("style-marculus.md § 2 : forme $nom absente")
                assertEquals(attendu.valeur.toFloat(), valeur, "$nom attendu ${attendu.valeur} dp (style-marculus.md L${attendu.numero}), obtenu $valeur")
            }
        }
    }

    @Test
    fun `valeurs en clair, pour l oeil du relecteur (4, 8, 12, 16, 28)`() {
        // style-marculus.md § 2, tableau « Défauts M3 1.4.0 » (4 dp, 8 dp, 12 dp, 16 dp, 28 dp).
        assertEquals(listOf(4f, 8f, 12f, 16f, 28f), formes.map { it.second })
    }

    @Test
    fun `la pilule est RoundedCornerShape 50, soit 50 pour cent (ecart E4)`() {
        // style-marculus.md § 2 : « pilule, RoundedCornerShape(50) » ; BadgeFix.kt L104 dans Marculus.
        assertEquals(50, FormesTerrain.pilulePourcent)
    }

    @Test
    fun `FormesTerrain n expose rien d autre que les 5 rayons et la pilule`() {
        val getters = FormesTerrain::class.java.methods
            .filter { it.parameterCount == 0 && it.name.startsWith("get") && it.declaringClass == FormesTerrain::class.java }
            .map { it.name.removePrefix("get").replaceFirstChar { c -> c.lowercase() } }
            .toSet()
        assertEquals(
            setOf("extraSmall", "small", "medium", "large", "extraLarge", "pilulePourcent"),
            getters,
            "membres publics de FormesTerrain (propriétés `val`, pas `const`)",
        )
    }
}
