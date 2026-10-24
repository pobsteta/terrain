package fr.nemeton.terrain.style

import org.junit.jupiter.api.DynamicTest
import org.junit.jupiter.api.TestFactory
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

/**
 * Boutons de Marculus (mission 021, critère 2 de docs/spec.md, ADR 0006).
 *
 * Valeurs lues dans `docs/referentiels/style-marculus.md` § 4 « Boutons : dimensions et formes »
 * (défauts Material 3 1.4.0, écart E4 : les boutons sont des pilules). Le référentiel compte
 * **19 lignes** de dimensions ; ce fichier en a une par ligne, numérotée de 1 à 19 dans l'ordre
 * du tableau. Tout en dp, en `Float`. Non couvert : l'élévation de `Card` (0) et
 * d'`ElevatedCard` (1), car la ligne du référentiel dit « élévation 0 / 1 dp » sans attribuer
 * chaque valeur à un composant.
 *
 * Contrat d'API (décision de l'orchestrateur, complété pour les valeurs sans nom imposé) :
 * `hauteurMin`, `largeurMin`, `tailleIcone`, `espaceIcone`, `epaisseurTrait`,
 * `margeHorizontale` (début et fin de Button, 24), `margeVerticale` (haut et bas, 8),
 * `margeHorizontaleAvecIcone` (début de Button avec icône, 16 ; sa fin reste `margeHorizontale`),
 * `margeHorizontaleTexte` (début et fin de TextButton, 12 ; début de TextButton avec icône),
 * `margeFinTexteAvecIcone` (fin de TextButton avec icône, 16), `elevation`, `elevationSurvol`,
 * `elevationContourEtTexte: Float?`, `styleTexte: StyleTexte`, `zoneTactileBoutonIcone`,
 * `conteneurBoutonIcone`, `fabTaille`, `fabTailleIcone`, `fabRayon`, `fabElevation`,
 * `petitFabTaille`, `petitFabTailleIcone`, `petitFabRayon`, `puceHauteur`, `puceTailleIcone`,
 * `puceRayon`, `carteRayon`. La forme (pilule) est `FormesTerrain.pilulePourcent`.
 */
class BoutonsTerrainTest {

    private class Ligne(val numero: Int, val libelle: String, val attendu: Any?, val obtenu: Any?)

    private val b = BoutonsTerrain

    /** Les 19 lignes du tableau du § 4, dans l'ordre du référentiel. */
    private fun lignes(): List<Ligne> = listOf(
        Ligne(1, "hauteur minimale des boutons", 40f, b.hauteurMin),
        Ligne(2, "largeur minimale", 58f, b.largeurMin),
        Ligne(3, "marges Button (début, haut, fin, bas)", listOf(24f, 8f, 24f, 8f), listOf(b.margeHorizontale, b.margeVerticale, b.margeHorizontale, b.margeVerticale)),
        Ligne(4, "marges Button avec icône", listOf(16f, 8f, 24f, 8f), listOf(b.margeHorizontaleAvecIcone, b.margeVerticale, b.margeHorizontale, b.margeVerticale)),
        Ligne(5, "marges TextButton", listOf(12f, 8f, 12f, 8f), listOf(b.margeHorizontaleTexte, b.margeVerticale, b.margeHorizontaleTexte, b.margeVerticale)),
        Ligne(6, "marges TextButton avec icône", listOf(12f, 8f, 16f, 8f), listOf(b.margeHorizontaleTexte, b.margeVerticale, b.margeFinTexteAvecIcone, b.margeVerticale)),
        Ligne(7, "taille de l'icône dans un bouton (ButtonDefaults.IconSize)", 18f, b.tailleIcone),
        Ligne(8, "espace icône-texte", 8f, b.espaceIcone),
        Ligne(9, "forme : pilule, RoundedCornerShape(50), en pourcent", 50, FormesTerrain.pilulePourcent),
        Ligne(10, "trait de OutlinedButton", 1f, b.epaisseurTrait),
        Ligne(11, "élévation de Button (repos)", 0f, b.elevation),
        Ligne(12, "élévation de OutlinedButton et TextButton : aucune", null, b.elevationContourEtTexte),
        Ligne(13, "style du texte : labelLarge (taille, interligne, graisse, interlettrage)", listOf(14f, 20f, 500, 0.1f), b.styleTexte.let { listOf(it.tailleSp, it.interligneSp, it.graisse, it.interlettrageSp) }),
        Ligne(14, "IconButton : zone tactile 48 x 48", 48f, b.zoneTactileBoutonIcone),
        Ligne(15, "IconButton : conteneur 40 x 40", 40f, b.conteneurBoutonIcone),
        Ligne(16, "FloatingActionButton : côté, icône, rayon, élévation", listOf(56f, 24f, 16f, 6f), listOf(b.fabTaille, b.fabTailleIcone, b.fabRayon, b.fabElevation)),
        Ligne(17, "SmallFloatingActionButton : côté, icône, rayon", listOf(40f, 24f, 12f), listOf(b.petitFabTaille, b.petitFabTailleIcone, b.petitFabRayon)),
        Ligne(18, "AssistChip, FilterChip : hauteur, icône, rayon", listOf(32f, 18f, 8f), listOf(b.puceHauteur, b.puceTailleIcone, b.puceRayon)),
        Ligne(19, "Card / ElevatedCard : rayon (élévation 0 / 1 dp : non couverte)", 12f, b.carteRayon),
    )

    @Test
    fun `le tableau du referentiel compte 19 lignes de dimensions, toutes couvertes`() {
        val lignes = lignes()
        assertEquals(19, lignes.size)
        assertEquals((1..19).toList(), lignes.map { it.numero })
        val lignesDuTableau = ReferentielMarculus.section("4.")
            .count { (_, l) -> l.startsWith("| ") && !l.startsWith("| Dimension") && !l.startsWith("|---") }
        assertEquals(19, lignesDuTableau, "lignes du tableau du § 4 de style-marculus.md")
    }

    @TestFactory
    fun `chaque dimension de bouton est egale au referentiel`(): List<DynamicTest> =
        lignes().map { l ->
            DynamicTest.dynamicTest("ligne ${l.numero} : ${l.libelle}") {
                assertEquals(l.attendu, l.obtenu, "ligne ${l.numero} (${l.libelle}) du tableau du § 4 de style-marculus.md")
            }
        }

    @Test
    fun `les rayons des composants reprennent les formes de Shapes`() {
        // style-marculus.md § 2, « Formes des composants » : FAB = large, petit FAB = medium,
        // puces = small, carte = medium.
        assertEquals(FormesTerrain.large, b.fabRayon)
        assertEquals(FormesTerrain.medium, b.petitFabRayon)
        assertEquals(FormesTerrain.small, b.puceRayon)
        assertEquals(FormesTerrain.medium, b.carteRayon)
    }

    @Test
    fun `le style du texte des boutons est labelLarge`() {
        // style-marculus.md § 4 : « Style du texte : labelLarge (14 sp / 20 sp / 500 / 0,1 sp) ».
        val s = b.styleTexte
        val l = TypoTerrain.labelLarge
        assertEquals(l.tailleSp, s.tailleSp)
        assertEquals(l.interligneSp, s.interligneSp)
        assertEquals(l.graisse, s.graisse)
        assertEquals(l.interlettrageSp, s.interlettrageSp)
    }

    @Test
    fun `OutlinedButton et TextButton n ont aucune elevation`() {
        assertNull(b.elevationContourEtTexte)
    }

    @Test
    fun `l elevation au survol de Button est 1 dp`() {
        // style-marculus.md § 4, ligne « Élévation Button » : « 0 dp (survol 1 dp) ».
        assertEquals(1f, b.elevationSurvol)
    }
}
