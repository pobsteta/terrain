package fr.nemeton.terrain.gnss

import fr.nemeton.terrain.geo.Position
import kotlin.math.sqrt

/** Trame GGA décodée : position + qualité du fix + métadonnées de qualité. */
data class TrameGga(
    val position: Position,
    val qualite: QualiteFix,
    val nbSatellites: Int,
    val hdop: Double?,
    val altitudeM: Double?,
    val ageCorrectionsS: Double?,
    val stationRef: String? = null,
    /** Heure UTC du jour du fix (ms depuis 00:00:00 UTC, champ 1 de la GGA) ; null si absente ou mal formée. */
    val heureJourMs: Long? = null,
)

/** Trame GSA décodée : dilutions de précision (position, horizontale, verticale). */
data class TrameGsa(val pdop: Double?, val hdop: Double?, val vdop: Double?)

/**
 * Trame RMC décodée : cap et vitesse **sur le fond** (déplacement). [capDeg] absent à l'arrêt
 * (champ vide), [vitesseMs] convertie depuis les nœuds. Sert à orienter le cône de direction.
 */
data class TrameRmc(
    val capDeg: Double?,
    val vitesseMs: Double?,
    /**
     * Instant UTC complet (date `ddmmyy` + heure, ms depuis l'époque Unix), renseigné **seulement
     * si le statut RMC est `A`** (fix valide) et la date et l'heure bien formées ; null sinon.
     */
    val instantUtcMs: Long? = null,
)

/** Une trame GSV (un message d'une séquence) : satellites en vue d'un système. */
data class TrameGsv(
    val systeme: String,
    val nbMessages: Int,
    val numMessage: Int,
    val satellitesEnVue: Int,
    val satellites: List<SatelliteGsv>,
)

/** Trame GST décodée : écarts-types de position (m) → précision horizontale estimée. */
data class TrameGst(
    val ecartTypeLatM: Double,
    val ecartTypeLonM: Double,
) {
    /** Précision horizontale (m) = norme des écarts-types latitude / longitude. */
    val precisionHorizontaleM: Double
        get() = sqrt(ecartTypeLatM * ecartTypeLatM + ecartTypeLonM * ecartTypeLonM)
}

/**
 * Analyse des trames NMEA 0183 d'un récepteur GNSS externe (Septentrio mosaic-X5, Unicore
 * UM980…). On n'exploite que **GGA** (position + qualité de fix) et **GST** (précision) ; le
 * talker (GP/GN/GL…) est ignoré. Tout est pur — testable en JVM, sans E/S ni dépendance Android.
 */
object NmeaParser {

    /** Un nœud (mille marin par heure) en mètres par seconde. */
    private const val NOEUD_EN_MS = 0.514444

    /** Somme de contrôle NMEA (XOR des octets du corps, hors `$` et `*hh`) en hexa deux chiffres. */
    fun sommeControle(corps: String): String {
        var c = 0
        for (ch in corps) c = c xor ch.code
        return "%02X".format(c)
    }

    /** Vrai si la somme de contrôle `*hh` (XOR des octets entre `$` et `*`) est correcte. */
    fun checksumValide(phrase: String): Boolean {
        val debut = phrase.indexOf('$')
        val etoile = phrase.lastIndexOf('*')
        if (debut < 0 || etoile < debut + 1 || etoile + 3 > phrase.length) return false
        val attendu = phrase.substring(etoile + 1, etoile + 3)
        return sommeControle(phrase.substring(debut + 1, etoile)).equals(attendu, ignoreCase = true)
    }

    /** Champs d'une trame valide (sans `$` ni `*hh`), ou null si la somme de contrôle est mauvaise. */
    private fun champs(phrase: String): List<String>? {
        if (!checksumValide(phrase)) return null
        val debut = phrase.indexOf('$')
        val etoile = phrase.lastIndexOf('*')
        return phrase.substring(debut + 1, etoile).split(',')
    }

    /** Décode une trame GGA ; null si ce n'est pas une GGA valide munie d'une position. */
    fun parseGga(phrase: String): TrameGga? {
        val f = champs(phrase) ?: return null
        if (f.isEmpty() || !f[0].endsWith("GGA")) return null
        val lat = coordonnee(f.getOrNull(2), f.getOrNull(3)) ?: return null
        val lon = coordonnee(f.getOrNull(4), f.getOrNull(5)) ?: return null
        return TrameGga(
            position = Position(lat, lon),
            qualite = QualiteFix.depuisCode(f.getOrNull(6)?.toIntOrNull() ?: 0),
            nbSatellites = f.getOrNull(7)?.toIntOrNull() ?: 0,
            hdop = f.getOrNull(8)?.toDoubleOrNull(),
            altitudeM = f.getOrNull(9)?.toDoubleOrNull(),
            ageCorrectionsS = f.getOrNull(13)?.toDoubleOrNull(),
            stationRef = f.getOrNull(14)?.takeIf { it.isNotBlank() },
            heureJourMs = heureJour(f.getOrNull(1)),
        )
    }

    /** Décode une trame GST ; null si ce n'est pas une GST valide. */
    fun parseGst(phrase: String): TrameGst? {
        val f = champs(phrase) ?: return null
        if (f.isEmpty() || !f[0].endsWith("GST")) return null
        val lat = f.getOrNull(6)?.toDoubleOrNull() ?: return null
        val lon = f.getOrNull(7)?.toDoubleOrNull() ?: return null
        return TrameGst(lat, lon)
    }

    /** Décode une trame GSA ; null si ce n'est pas une GSA valide. PDOP/HDOP/VDOP en fin de trame. */
    fun parseGsa(phrase: String): TrameGsa? {
        val f = champs(phrase) ?: return null
        if (f.isEmpty() || !f[0].endsWith("GSA")) return null
        return TrameGsa(
            pdop = f.getOrNull(15)?.toDoubleOrNull(),
            hdop = f.getOrNull(16)?.toDoubleOrNull(),
            vdop = f.getOrNull(17)?.toDoubleOrNull(),
        )
    }

    /**
     * Décode une trame RMC ; null si ce n'est pas une RMC valide. Champs : 7 = vitesse fond (nœuds),
     * 8 = cap fond (° vrais). Le cap n'est défini que si le récepteur se déplace.
     */
    fun parseRmc(phrase: String): TrameRmc? {
        val f = champs(phrase) ?: return null
        if (f.isEmpty() || !f[0].endsWith("RMC")) return null
        val vitesseNoeuds = f.getOrNull(7)?.toDoubleOrNull()
        val instant = if (f.getOrNull(2) == "A") {
            val jour = jourEpoch(f.getOrNull(9))
            val heure = heureJour(f.getOrNull(1))
            if (jour != null && heure != null) jour * JOUR_MS + heure else null
        } else {
            null
        }
        return TrameRmc(
            capDeg = f.getOrNull(8)?.toDoubleOrNull(),
            vitesseMs = vitesseNoeuds?.let { it * NOEUD_EN_MS },
            instantUtcMs = instant,
        )
    }

    /** Décode une trame GSV (satellites en vue) ; null si ce n'est pas une GSV valide. */
    fun parseGsv(phrase: String): TrameGsv? {
        val f = champs(phrase) ?: return null
        if (f.isEmpty() || !f[0].endsWith("GSV")) return null
        val systeme = f[0].dropLast(3)
        val nbMessages = f.getOrNull(1)?.toIntOrNull() ?: return null
        val numMessage = f.getOrNull(2)?.toIntOrNull() ?: return null
        val enVue = f.getOrNull(3)?.toIntOrNull() ?: 0
        val sats = mutableListOf<SatelliteGsv>()
        var i = 4
        while (i < f.size) { // groupes de 4 (PRN, élévation, azimut, SNR) ; SNR final parfois absent
            val prn = f.getOrNull(i)?.toIntOrNull() ?: break
            sats += SatelliteGsv(
                prn = prn,
                elevation = f.getOrNull(i + 1)?.toIntOrNull(),
                azimut = f.getOrNull(i + 2)?.toIntOrNull(),
                snr = f.getOrNull(i + 3)?.toIntOrNull(),
                systeme = systeme,
            )
            i += 4
        }
        return TrameGsv(systeme, nbMessages, numMessage, enVue, sats)
    }

    /** Assemble un fix complet depuis une GGA et (optionnellement) les dernières GST, GSA et RMC. */
    fun fixDepuis(
        gga: TrameGga,
        gst: TrameGst? = null,
        gsa: TrameGsa? = null,
        rmc: TrameRmc? = null,
        satellites: List<SatelliteGsv> = emptyList(),
        recuMonotoneMs: Long? = null,
    ): FixGnss = FixGnss(
        position = gga.position,
        qualite = gga.qualite,
        nbSatellites = gga.nbSatellites,
        hdop = gga.hdop ?: gsa?.hdop,
        altitudeM = gga.altitudeM,
        ageCorrectionsS = gga.ageCorrectionsS,
        precisionHorizontaleM = gst?.precisionHorizontaleM,
        pdop = gsa?.pdop,
        vdop = gsa?.vdop,
        stationRef = gga.stationRef,
        capDeg = rmc?.capDeg,
        vitesseMs = rmc?.vitesseMs,
        satellites = satellites,
        heureUtcMs = heureUtcComplete(gga.heureJourMs, rmc),
        recuMonotoneMs = recuMonotoneMs,
    )

    private const val JOUR_MS = 86_400_000L

    /** `hhmmss` ou `hhmmss.ss` → ms depuis minuit UTC ; null si mal formé. */
    internal fun heureJour(v: String?): Long? {
        if (v == null || v.length < 6 || !v.take(6).all { it.isDigit() }) return null
        val h = v.substring(0, 2).toInt()
        val m = v.substring(2, 4).toInt()
        val s = v.substring(4, 6).toInt()
        if (h > 23 || m > 59 || s > 60) return null
        val frac = if (v.length > 6) {
            if (v[6] != '.' || v.length == 7 || !v.substring(7).all { it.isDigit() }) return null
            Math.round(("0." + v.substring(7)).toDouble() * 1000)
        } else {
            0L
        }
        return ((h * 60L + m) * 60 + s) * 1000 + frac
    }

    /** `ddmmyy` (année 20yy) → jours depuis 1970-01-01 ; null si mal formé. */
    internal fun jourEpoch(v: String?): Long? {
        if (v == null || v.length != 6 || !v.all { it.isDigit() }) return null
        val j = v.substring(0, 2).toInt()
        val mo = v.substring(2, 4).toInt()
        val a = 2000 + v.substring(4, 6).toInt()
        if (mo !in 1..12 || j !in 1..31) return null
        // Jours depuis l'époque, algorithme « days from civil » (H. Hinnant).
        val y = if (mo <= 2) a - 1 else a
        val era = y / 400
        val yoe = y - era * 400
        val doy = (153 * (mo + (if (mo > 2) -3 else 9)) + 2) / 5 + j - 1
        val doe = yoe * 365 + yoe / 4 - yoe / 100 + doy
        return era * 146097L + doe - 719468
    }

    /**
     * Heure UTC complète d'une GGA : son heure du jour sur le jour de la RMC. Autour de minuit UTC,
     * on retient le jour (veille, même jour, lendemain) qui rapproche le plus la GGA de l'instant
     * de la RMC. Null sans RMC valide (statut `A`) ou sans heure de GGA.
     */
    internal fun heureUtcComplete(heureJourMs: Long?, rmc: TrameRmc?): Long? {
        val r = rmc?.instantUtcMs ?: return null
        if (heureJourMs == null) return null
        val jourRmc = Math.floorDiv(r, JOUR_MS)
        return (-1L..1L).map { (jourRmc + it) * JOUR_MS + heureJourMs }.minByOrNull { kotlin.math.abs(it - r) }
    }

    /** Convertit une coordonnée NMEA `ddmm.mmmm` + hémisphère en degrés décimaux signés. */
    private fun coordonnee(valeur: String?, hemisphere: String?): Double? {
        val v = valeur?.toDoubleOrNull() ?: return null
        if (hemisphere.isNullOrBlank()) return null
        val deg = (v / 100).toInt()
        val min = v - deg * 100
        val dec = deg + min / 60.0
        return when (hemisphere.uppercase()) {
            "N", "E" -> dec
            "S", "W" -> -dec
            else -> null
        }
    }
}
