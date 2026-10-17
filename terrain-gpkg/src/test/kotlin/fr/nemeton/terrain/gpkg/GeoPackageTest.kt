package fr.nemeton.terrain.gpkg

import fr.nemeton.terrain.geo.Position
import java.io.File
import java.nio.file.Files
import kotlin.math.abs
import kotlin.test.AfterTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNull
import kotlin.test.assertTrue

/**
 * Sur `gpkg/essai.gpkg` (fabriqué par GDAL, voir `fabriquer.sh`) : chaque sommet reprojeté doit
 * égaler celui que PROJ donne dans `reference.csv`.
 */
class GeoPackageTest {
    private val copie: File = Files.createTempFile("essai", ".gpkg").toFile().also { f ->
        javaClass.getResourceAsStream("/gpkg/essai.gpkg")!!.use { e -> f.outputStream().use { e.copyTo(it) } }
    }
    private val sqlite = SqliteJdbc(copie.absolutePath)
    private val gpkg = GeoPackage(sqlite.base)

    @AfterTest
    fun fermer() {
        sqlite.close()
        copie.delete()
    }

    private data class Sommet(val couche: String, val fid: Long, val partie: Int, val index: Int, val lon: Double, val lat: Double)

    private val reference = javaClass.getResource("/gpkg/reference.csv")!!.readText().lines().drop(1).filter { it.isNotBlank() }.map {
        val v = it.split(';')
        Sommet(v[0], v[1].toLong(), v[2].toInt(), v[3].toInt(), v[4].toDouble(), v[5].toDouble())
    }

    @Test
    fun `couches, colonnes et systemes`() {
        val c = gpkg.couches().associateBy { it.table }
        assertEquals(listOf("parcelle", "desserte", "houppier", "points_wgs84", "points_wm", "points_3d", "points_utm"), gpkg.couches().map { it.table })
        assertEquals("geometry", c.getValue("parcelle").colonneGeometrie)
        assertEquals("fid", c.getValue("parcelle").colonneId)
        assertEquals(Systeme.LAMBERT93, c.getValue("parcelle").systeme)
        assertEquals(Systeme.WGS84, c.getValue("points_wgs84").systeme)
        assertEquals(Systeme.WEB_MERCATOR, c.getValue("points_wm").systeme)
        assertNull(c.getValue("points_utm").systeme)
        assertEquals("EPSG:32631", c.getValue("points_utm").descriptionSysteme)
    }

    @Test
    fun `sommets reprojetes identiques a PROJ`() {
        val couches = gpkg.couches().associateBy { it.table }
        var n = 0
        reference.groupBy { it.couche }.forEach { (table, sommets) ->
            val couche = couches.getValue(table)
            val entites = gpkg.entites(couche).associateBy { it.id }
            sommets.forEach { s ->
                val g = entites.getValue(s.fid).geometrie!!
                val parties = when (g) {
                    is Geometrie.Point -> listOf(listOf(g))
                    else -> g.lignes()
                }
                val p = couche.systeme!!.versWgs84(parties[s.partie][s.index])
                // 1e-9° ≈ 0,1 mm : tolérance des projections de terrain-gnss contre PROJ.
                assertTrue(abs(p.longitude - s.lon) < 1e-9 && abs(p.latitude - s.lat) < 1e-9, "$s → $p")
                n++
            }
        }
        assertEquals(reference.size, n)
        assertTrue(n >= 30)
    }

    @Test
    fun `attributs - texte, reel, booleen, vide garde`() {
        val parcelle = gpkg.entites(gpkg.couche("parcelle")!!).first()
        assertEquals(mapOf("fid" to "1", "proprietaire" to "Commune de Dijon", "foret" to "Personnelle", "parcelle" to "1"), parcelle.attributs)
        val dessertes = gpkg.entites(gpkg.couche("desserte")!!)
        assertEquals("true", dessertes[0].attributs["ouverte"])
        assertEquals("false", dessertes[1].attributs["ouverte"])
        assertEquals("", dessertes[1].attributs["nom"])
        assertEquals("24.5", gpkg.entites(gpkg.couche("houppier")!!).single().attributs["h_max"])
    }

    @Test
    fun `formes - multipolygone, polygone a trou, multiligne, point 3D`() {
        val houppier = gpkg.entites(gpkg.couche("houppier")!!).single()
        assertEquals(2, houppier.geometrie!!.anneaux().size)
        val dessertes = gpkg.entites(gpkg.couche("desserte")!!)
        assertEquals(listOf(1, 2), dessertes.map { it.geometrie!!.lignes().size })
        val borne = gpkg.entites(gpkg.couche("points_3d")!!).single().geometrie
        assertEquals(Geometrie.Point(853412.31, 6693028.77), borne)
        assertTrue(gpkg.entites(gpkg.couche("parcelle")!!).all { it.anneauxWgs84(Systeme.LAMBERT93).isNotEmpty() })
    }

    @Test
    fun `systeme non gere refuse`() {
        val e = assertFailsWith<SystemeNonGere> { gpkg.entites(gpkg.couche("points_utm")!!) }
        assertTrue("EPSG:32631" in e.message!!)
    }

    @Test
    fun `ecriture - entite relue, transaction annulee sur erreur`() {
        val couche = gpkg.couche("houppier")!!
        val centre = Systeme.LAMBERT93.depuisWgs84(Position(47.3197, 5.0312))
        val carre = Geometrie.Polygone(
            listOf(listOf(centre, centre.copy(x = centre.x + 2), centre.copy(x = centre.x + 2, y = centre.y + 2), centre)),
        )
        val id = gpkg.inserer(couche, carre, mapOf("h_max" to 18.0))
        val relue = gpkg.entites(couche).single { it.id == id }
        assertEquals(carre, relue.geometrie)
        assertEquals("18.0", relue.attributs["h_max"])
        // Ligne d'index spatial écrite, déclencheurs remis.
        val index = sqlite.base.requete("SELECT minx, maxx FROM rtree_houppier_geom WHERE id = ?", listOf(id)).single()
        assertEquals(centre.x, index["minx"] as Double, 1.0) // R-tree en flottants 32 bits
        assertEquals(centre.x + 2, index["maxx"] as Double, 1.0)
        val declencheurs = sqlite.base.requete("SELECT name FROM sqlite_master WHERE type = 'trigger' AND tbl_name = 'houppier'")
        assertTrue(declencheurs.size >= 4)
        assertEquals("ok", sqlite.base.requete("PRAGMA integrity_check").single().values.single())
        // TERRAIN_GPKG_ECRIT=<chemin> garde le fichier écrit, pour le relire avec GDAL (ogrinfo).
        System.getenv("TERRAIN_GPKG_ECRIT")?.let { copie.copyTo(File(it), overwrite = true) }
        val avant = gpkg.entites(couche).size
        assertFailsWith<Exception> { gpkg.inserer(couche, carre, mapOf("colonne_absente" to 1)) }
        assertEquals(avant, gpkg.entites(couche).size)
    }
}
