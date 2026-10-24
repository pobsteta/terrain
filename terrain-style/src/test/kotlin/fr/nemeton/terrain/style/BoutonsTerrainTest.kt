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
 * du tableau. Tout en dp (`Int`), sauf indication. Non couvert : l'élévation de `Card` (0) et
 * d'`ElevatedCard` (1), car la ligne du référentiel dit « élévation 0 / 1 dp » sans attribuer
 * chaque valeur à un composant.
 *
 * Contrat d'API attendu (voir le corps de la PR) : `BoutonsTerrain.Marges(debutDp, hautDp, finDp, basDp)`
 * (`Int`) et les propriétés `Int` ci-dessous, plus `styleTexte: TypoTerrain.Style` et
 * `elevationContourEtTexteDp: Int?` (`null` = aucune élévation, `elevation = null` dans Material).
 */
class BoutonsTerrainTest {

    private class Ligne(val numero: Int, val libelle: String, val attendu: Any?, val obtenu: Any?)

    private fun marges(m: BoutonsTerrain.Marges) = listOf(m.debutDp, m.hautDp, m.finDp, m.basDp)

    /** Les 19 lignes du tableau du § 4, dans l'ordre du référentiel. */
    private fun lignes(): List<Ligne> = listOf(
        Ligne(1, "hauteur minimale des boutons", 40, BoutonsTerrain.hauteurMinDp),
        Ligne(2, "largeur minimale", 58, BoutonsTerrain.largeurMinDp),
        Ligne(3, "marges Button/OutlinedButton (début, haut, fin, bas)", listOf(24, 8, 24, 8), marges(BoutonsTerrain.margesBouton)),
        Ligne(4, "marges Button avec icône", listOf(16, 8, 24, 8), marges(BoutonsTerrain.margesBoutonAvecIcone)),
        Ligne(5, "marges TextButton", listOf(12, 8, 12, 8), marges(BoutonsTerrain.margesBoutonTexte)),
        Ligne(6, "marges TextButton avec icône", listOf(12, 8, 16, 8), marges(BoutonsTerrain.margesBoutonTexteAvecIcone)),
        Ligne(7, "taille de l'icône dans un bouton (ButtonDefaults.IconSize)", 18, BoutonsTerrain.iconeDp),
        Ligne(8, "espace icône-texte", 8, BoutonsTerrain.espaceIconeTexteDp),
        Ligne(9, "forme : pilule, RoundedCornerShape(50), en pourcent", 50, BoutonsTerrain.formePilulePourcent),
        Ligne(10, "trait de OutlinedButton", 1, BoutonsTerrain.traitContourDp),
        Ligne(11, "élévation de Button (repos)", 0, BoutonsTerrain.elevationDp),
        Ligne(12, "élévation de OutlinedButton et TextButton : aucune", null, BoutonsTerrain.elevationContourEtTexteDp),
        Ligne(13, "style du texte : labelLarge (taille, interligne, graisse, interlettrage)", listOf(14, 20, 500, 0.1), BoutonsTerrain.styleTexte.let { listOf(it.tailleSp, it.interligneSp, it.graisse, it.interlettrageSp) }),
        Ligne(14, "IconButton : zone tactile 48 x 48", 48, BoutonsTerrain.iconBoutonZoneTactileDp),
        Ligne(15, "IconButton : conteneur 40 x 40", 40, BoutonsTerrain.iconBoutonConteneurDp),
        Ligne(16, "FloatingActionButton : côté, icône, rayon, élévation", listOf(56, 24, 16, 6), listOf(BoutonsTerrain.fabDp, BoutonsTerrain.fabIconeDp, BoutonsTerrain.fabRayonDp, BoutonsTerrain.fabElevationDp)),
        Ligne(17, "SmallFloatingActionButton : côté, icône, rayon", listOf(40, 24, 12), listOf(BoutonsTerrain.petitFabDp, BoutonsTerrain.petitFabIconeDp, BoutonsTerrain.petitFabRayonDp)),
        Ligne(18, "AssistChip, FilterChip : hauteur, icône, rayon", listOf(32, 18, 8), listOf(BoutonsTerrain.puceHauteurDp, BoutonsTerrain.puceIconeDp, BoutonsTerrain.puceRayonDp)),
        Ligne(19, "Card / ElevatedCard : rayon (élévation 0 / 1 dp : voir ci-dessous)", 12, BoutonsTerrain.carteRayonDp),
    )

    @Test
    fun `le tableau du referentiel compte 19 lignes de dimensions, toutes couvertes`() {
        // Le décompte « 19 lignes » est dit par le référentiel (§ 4, « Décompte des dimensions de boutons relevées »).
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
    fun `la forme du bouton est celle de FormesTerrain pilule`() {
        assertEquals(FormesTerrain.pilulePourcent, BoutonsTerrain.formePilulePourcent)
    }

    @Test
    fun `les rayons des composants reprennent les formes de Shapes`() {
        // style-marculus.md § 2, « Formes des composants » : FAB = large, petit FAB = medium,
        // puces = small, carte = medium.
        assertEquals(FormesTerrain.large, BoutonsTerrain.fabRayonDp)
        assertEquals(FormesTerrain.medium, BoutonsTerrain.petitFabRayonDp)
        assertEquals(FormesTerrain.small, BoutonsTerrain.puceRayonDp)
        assertEquals(FormesTerrain.medium, BoutonsTerrain.carteRayonDp)
    }

    @Test
    fun `le style du texte des boutons est labelLarge`() {
        // style-marculus.md § 4 : « Style du texte : labelLarge (14 sp / 20 sp / 500 / 0,1 sp) ».
        val s = BoutonsTerrain.styleTexte
        val l = TypoTerrain.labelLarge
        assertEquals(l.tailleSp, s.tailleSp)
        assertEquals(l.interligneSp, s.interligneSp)
        assertEquals(l.graisse, s.graisse)
        assertEquals(l.interlettrageSp, s.interlettrageSp)
    }

    @Test
    fun `OutlinedButton et TextButton n ont aucune elevation`() {
        assertNull(BoutonsTerrain.elevationContourEtTexteDp)
    }

    @Test
    fun `l elevation au survol de Button est 1 dp`() {
        // style-marculus.md § 4, ligne « Élévation Button » : « 0 dp (survol 1 dp) ».
        assertEquals(1, BoutonsTerrain.elevationSurvolDp)
    }
}
