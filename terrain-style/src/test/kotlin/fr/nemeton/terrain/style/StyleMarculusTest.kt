package fr.nemeton.terrain.style

import org.junit.jupiter.api.DynamicTest
import org.junit.jupiter.api.TestFactory
import kotlin.test.Test
import kotlin.test.assertEquals

/**
 * Égalité des couleurs au style de Marculus (mission 021, critère 2 de docs/spec.md, ADR 0006).
 *
 * Écrit sans lire l'implémentation. Les 48 rôles x 2 thèmes viennent de
 * `jetons/style-marculus.csv` (96 lignes, recopié du référentiel `docs/referentiels/style-marculus.md`
 * § 1, relevé dans Marculus v0.65.0 et Compose Material 3 1.4.0). `reussi` et `alerte`
 * (16 valeurs) ne sont pas dans ce CSV : voir [semantiquesAttendues].
 */
class StyleMarculusTest {

    private val roles: List<Pair<String, (Schema) -> Long>> = listOf(
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

    private val themes = listOf("clair" to StyleTerrain.clair, "sombre" to StyleTerrain.sombre)

    private fun hex(v: Long) = "0x" + java.lang.Long.toHexString(v).uppercase().padStart(8, '0')

    @Test
    fun `le CSV du referentiel compte 96 lignes, 48 roles par theme, les 48 roles de ColorScheme`() {
        val csv = ReferentielMarculus.couleurs()
        assertEquals(96, csv.size)
        assertEquals(48, roles.size)
        assertEquals(setOf("clair", "sombre"), csv.keys.map { it.first }.toSet())
        for ((theme, _) in themes) {
            val duTheme = csv.keys.filter { it.first == theme }.map { it.second }
            assertEquals(48, duTheme.size, "rôles du thème $theme dans le CSV")
            assertEquals(roles.map { it.first }.toSet(), duTheme.toSet(), "rôles du thème $theme dans le CSV")
        }
    }

    @TestFactory
    fun `chacun des 96 roles est egal a la valeur du referentiel (CSV)`(): List<DynamicTest> {
        val csv = ReferentielMarculus.couleurs()
        return themes.flatMap { (theme, schema) ->
            roles.map { (nom, lire) ->
                DynamicTest.dynamicTest("$theme : $nom") {
                    val attendu = csv[theme to nom] ?: error("style-marculus.csv : $theme,$nom absent")
                    assertEquals(
                        attendu.valeur,
                        lire(schema),
                        "$theme : $nom attendu ${hex(attendu.valeur)} (style-marculus.csv L${attendu.numero}), " +
                            "obtenu ${hex(lire(schema))}",
                    )
                }
            }
        }
    }

    /**
     * `reussi` : valeurs de v0.6.1 gelées le 2026-10-24 (ADR 0002, note « Gel » ; spec « Réussi »),
     * lues dans `jetons/jetons.csv` L50-53 (clair) et L106-109 (sombre).
     * `alerte` : jaune de sécurité ISO 3864-4 `#E2AA00` en couleur et conteneur, noir en sur-couleur
     * (spec « Alerte » ; `docs/referentiels/couleurs-etat.md`), identique dans les deux thèmes
     * (`jetons/jetons.csv` L54-57 et L110-113).
     * Quatre valeurs par entrée : couleur, surCouleur, conteneur, surConteneur.
     */
    private val semantiquesAttendues: Map<String, List<Long>> = mapOf(
        "clair reussi" to listOf(0xFF00341FL, 0xFFFFFFFFL, 0xFF005436L, 0xFFFFFFFFL),
        "sombre reussi" to listOf(0xFFBBFFD7L, 0xFF000000L, 0xFF71D6A2L, 0xFF000E06L),
        "clair alerte" to listOf(0xFFE2AA00L, 0xFF000000L, 0xFFE2AA00L, 0xFF000000L),
        "sombre alerte" to listOf(0xFFE2AA00L, 0xFF000000L, 0xFFE2AA00L, 0xFF000000L),
    )

    @TestFactory
    fun `reussi et alerte (16 valeurs) sont egales aux valeurs gelees`(): List<DynamicTest> =
        themes.flatMap { (theme, schema) ->
            listOf("reussi" to schema.reussi, "alerte" to schema.alerte).flatMap { (nom, s) ->
                val attendu = semantiquesAttendues.getValue("$theme $nom")
                listOf(
                    "couleur" to s.couleur, "surCouleur" to s.surCouleur,
                    "conteneur" to s.conteneur, "surConteneur" to s.surConteneur,
                ).mapIndexed { i, (champ, valeur) ->
                    DynamicTest.dynamicTest("$theme : $nom.$champ") {
                        assertEquals(attendu[i], valeur, "$theme : $nom.$champ attendu ${hex(attendu[i])}, obtenu ${hex(valeur)}")
                    }
                }
            }
        }

    @Test
    fun `l erreur est error du schema, aux valeurs par defaut de Material 3`() {
        assertEquals(0xFFB3261EL, StyleTerrain.clair.error)   // style-marculus.csv L24
        assertEquals(0xFFF2B8B5L, StyleTerrain.sombre.error)  // style-marculus.csv L72
    }

    @Test
    fun `le vert de Marculus est primary dans les deux themes, et surfaceTint le suit (ecart E1)`() {
        // style-marculus.csv L2 et L21 (clair), L50 et L69 (sombre) ; référentiel, « Écarts » E1.
        for ((_, s) in themes) {
            assertEquals(0xFF3E7A40L, s.primary)
            assertEquals(0xFF3E7A40L, s.surfaceTint)
        }
    }
}
