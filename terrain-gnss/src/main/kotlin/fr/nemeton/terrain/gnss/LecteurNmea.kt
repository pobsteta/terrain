package fr.nemeton.terrain.gnss

/**
 * Transforme le flux NMEA d'un récepteur en [FixGnss] : chaque GGA est complétée par les
 * dernières GST (précision), GSA (dilutions), RMC (cap, vitesse, date) et les dernières séquences
 * GSV **complètes** (skyplot). S'appuie sur [NmeaDecoupeur] pour recoller les trames fragmentées.
 * Pur, sans E/S → testable en JVM.
 *
 * @param horlogeMonotoneMs horloge monotone (ms, p. ex. `SystemClock.elapsedRealtime`) lue à chaque
 *   GGA et posée dans [FixGnss.recuMonotoneMs] ; null = pas d'horodatage monotone.
 */
class LecteurNmea(private val horlogeMonotoneMs: (() -> Long)? = null) {
    private val decoupeur = NmeaDecoupeur()
    private var gst: TrameGst? = null
    private var gsa: TrameGsa? = null
    private var rmc: TrameRmc? = null
    private val enCours = HashMap<String, MutableList<SatelliteGsv>>()
    private val complets = HashMap<String, List<SatelliteGsv>>()

    /** Dernière GGA telle que reçue, à renvoyer au caster NTRIP (VRS / NEAR). */
    var derniereGga: String? = null
        private set

    /** Pousse du texte reçu ; renvoie les trames qu'il termine et les fix qu'elles donnent. */
    fun pousser(texte: String): Pair<List<String>, List<FixGnss>> {
        val trames = decoupeur.pousser(texte)
        val fixes = mutableListOf<FixGnss>()
        for (t in trames) {
            NmeaParser.parseGst(t)?.let { gst = it }
            NmeaParser.parseGsa(t)?.let { gsa = it }
            NmeaParser.parseRmc(t)?.let { rmc = it }
            NmeaParser.parseGsv(t)?.let { g ->
                // Un message n° 1 ouvre une nouvelle séquence ; la séquence n'est publiée qu'une fois complète.
                val tampon = if (g.numMessage <= 1) {
                    mutableListOf<SatelliteGsv>().also { enCours[g.systeme] = it }
                } else {
                    enCours.getOrPut(g.systeme) { mutableListOf() }
                }
                tampon += g.satellites
                if (g.numMessage >= g.nbMessages) complets[g.systeme] = tampon.toList()
            }
            NmeaParser.parseGga(t)?.let { g ->
                derniereGga = t
                fixes += NmeaParser.fixDepuis(g, gst, gsa, rmc, complets.values.flatten(), horlogeMonotoneMs?.invoke())
            }
        }
        return trames to fixes
    }
}
