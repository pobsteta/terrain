package fr.nemeton.terrain.carte

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class FondsEchelleTest {

    @Test
    fun `URL WMTS - identique a celle que Marculus et Groma construisaient`() {
        assertEquals(
            "https://data.geopf.fr/wmts?SERVICE=WMTS&REQUEST=GetTile&VERSION=1.0.0" +
                "&LAYER=CADASTRALPARCELS.PARCELLAIRE_EXPRESS&STYLE=PCI%20vecteur&FORMAT=image/png" +
                "&TILEMATRIXSET=PM_0_19&TILEMATRIX=15&TILEROW=11500&TILECOL=16900",
            Geoplateforme.CADASTRE.url(15, 16900, 11500),
        )
        assertEquals(".jpg", Geoplateforme.ORTHO_IGN.extension)
        assertEquals(".png", Geoplateforme.PLAN_IGN.extension)
        assertEquals(Geoplateforme.ORTHO_IGN, Geoplateforme.parNom("OrthoIGN"))
        assertNull(Geoplateforme.parNom("Mapnik"))
        assertEquals(Attributions.IGN, Geoplateforme.PLAN_IGN.attribution)
    }

    @Test
    fun `echelle - distance ronde, libelle, resolution`() {
        assertEquals(50.0, Echelle.distanceRonde(73.0))
        assertEquals(200.0, Echelle.distanceRonde(399.0))
        assertEquals(1000.0, Echelle.distanceRonde(1999.0))
        assertEquals(0.0, Echelle.distanceRonde(0.0))
        assertEquals("200 m", Echelle.libelle(200.0))
        assertEquals("1 km", Echelle.libelle(1000.0))
        assertEquals("2.5 km", Echelle.libelle(2500.0))
        assertEquals(156543.03392, Echelle.resolutionAuSol(0.0, 0.0), 1e-9)
        assertEquals(156543.03392 / 2 / 1024, Echelle.resolutionAuSol(60.0, 10.0), 1e-6)
    }
}
