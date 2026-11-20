package fr.nemeton.terrain.gnss

import kotlin.math.abs
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

/** Porté de Groma (`NmeaTest.lecteurCompleteLesFix`, `fixComplet`), noms de terrain. */
class LecteurNmeaTest {
    private val ggaRtkFixe = "\$GNGGA,141322.00,4717.112671,N,00833.914843,E,4,12,0.65,123.456,M,47.5,M,1.0,0000*62"
    private val ggaAutonome = "\$GPGGA,123519,4807.038,N,01131.000,E,1,08,0.9,545.4,M,46.9,M,,*47"
    private val gst = "\$GPGST,172814.0,0.006,0.023,0.020,273.6,0.023,0.020,0.031*6A"
    private val gsv = "\$GPGSV,3,1,11,03,03,111,00,04,15,270,00*7F"
    private val gsa = "\$GNGSA,A,3,01,02,03,04,05,06,07,08,09,10,11,12,1.21,0.65,1.02*1D"
    private val rmcMouvement = "\$GPRMC,123519,A,4807.038,N,01131.000,E,022.4,084.4,230394,003.1,W*6A"

    private fun proche(attendu: Double, obtenu: Double?, tol: Double = 1e-5) =
        assertTrue(obtenu != null && abs(attendu - obtenu) <= tol, "attendu ~$attendu, obtenu $obtenu")

    @Test
    fun `fixDepuis complet`() {
        val f = NmeaParser.fixDepuis(
            NmeaParser.parseGga(ggaRtkFixe)!!, NmeaParser.parseGst(gst), NmeaParser.parseGsa(gsa), NmeaParser.parseRmc(rmcMouvement),
        )
        assertEquals(QualiteFix.RTK_FIXE, f.qualite)
        proche(0.030480, f.precisionHorizontaleM)
        proche(84.4, f.capDeg, 1e-6)
        assertNull(NmeaParser.fixDepuis(NmeaParser.parseGga(ggaAutonome)!!).precisionHorizontaleM)
    }

    @Test
    fun `le lecteur complete les fix, trames coupees n'importe ou`() {
        val l = LecteurNmea()
        val flux = "$gst\r\n$gsv\r\n$gsa\r\n$ggaRtkFixe\r\n"
        val f = flux.chunked(7).flatMap { l.pousser(it).second }.single()
        assertEquals(QualiteFix.RTK_FIXE, f.qualite)
        proche(0.030480, f.precisionHorizontaleM)
        proche(1.21, f.pdop, 1e-6)
        // Séquence GSV 1 sur 3 incomplète : aucun satellite publié.
        assertEquals(emptyList(), f.satellites)
        assertEquals(ggaRtkFixe, l.derniereGga)
        // Séquence d'un message, complète : conservée pour le fix suivant.
        val corps = "GLGSV,1,1,01,65,40,120,35"
        val gsvSeul = "\$$corps*${NmeaParser.sommeControle(corps)}"
        assertEquals(listOf(65), l.pousser("$gsvSeul\r\n$ggaAutonome\r\n").second.single().satellites.map { it.prn })
    }
}
