package fr.nemeton.terrain.voix

/**
 * Nombres français en jetons séparés (pas de traits d'union : Vosk sort des mots), 1..999.
 * Sans accent ni « s » de pluriel, comme le modèle `vosk-model-small-fr` les restitue.
 */
object NombresFrancais {
    private val unites = arrayOf(
        "", "un", "deux", "trois", "quatre", "cinq", "six", "sept", "huit", "neuf",
        "dix", "onze", "douze", "treize", "quatorze", "quinze", "seize",
        "dix sept", "dix huit", "dix neuf",
    )

    fun jetons(n: Int): List<String> {
        require(n in 1..999) { "Nombre hors bornes : $n" }
        val mots = when {
            n < 20 -> unites[n]
            n < 70 -> dizaine(n / 10) + unite(n % 10)
            n < 80 -> "soixante" + adolescent(n - 60)
            n < 100 -> "quatre vingt" + if (n == 80) "" else " " + unites[n - 80]
            // Centaines sans le « s » du pluriel : Vosk restitue « cent » dans les deux cas.
            else -> {
                val tete = if (n / 100 == 1) "cent" else unites[n / 100] + " cent"
                if (n % 100 == 0) tete else tete + " " + jetons(n % 100).joinToString(" ")
            }
        }
        return mots.trim().split(" ")
    }

    private fun dizaine(d: Int) = arrayOf("", "", "vingt", "trente", "quarante", "cinquante", "soixante")[d]
    private fun unite(u: Int) = when (u) { 0 -> ""; 1 -> " et un"; else -> " " + unites[u] }
    private fun adolescent(v: Int) = if (v == 11) " et onze" else " " + unites[v]
}

/**
 * Alphabet radio : le mot de l'OTAN d'abord, puis des replis présents dans les modèles français
 * quand il en est absent (« foxtrot », « juliett », « uniform » et « xray » manquent au modèle
 * small-fr). Une lettre nue est le pire cas en reconnaissance : on épelle en radio.
 */
object AlphabetRadio {
    private val VARIANTES: Map<Char, List<String>> = mapOf(
        'A' to listOf("alpha"),
        'B' to listOf("bravo"),
        'C' to listOf("charlie"),
        'D' to listOf("delta"),
        'E' to listOf("echo"),
        'F' to listOf("foxtrot", "fox"),
        'G' to listOf("golf"),
        'H' to listOf("hotel"),
        'I' to listOf("india"),
        'J' to listOf("juliett", "juliette"),
        'K' to listOf("kilo"),
        'L' to listOf("lima"),
        'M' to listOf("mike"),
        'N' to listOf("november"),
        'O' to listOf("oscar"),
        'P' to listOf("papa"),
        'Q' to listOf("quebec"),
        'R' to listOf("romeo"),
        'S' to listOf("sierra"),
        'T' to listOf("tango"),
        'U' to listOf("uniform", "uniforme"),
        'V' to listOf("victor"),
        'W' to listOf("whisky"),
        'X' to listOf("xray", "xavier"),
        'Y' to listOf("yankee"),
        'Z' to listOf("zoulou"),
    )

    /** Premier choix par lettre, pour l'affichage. */
    val CANONIQUE: Map<Char, String> = VARIANTES.mapValues { it.value.first() }

    /** Tous les mots, variantes comprises : à confronter au vocabulaire du modèle. */
    val TOUS_LES_MOTS: Set<String> = VARIANTES.values.flatten().toSet()

    /**
     * Mot radio de [lettre] (insensible à la casse), en préférant une variante que le modèle sait
     * décoder ([motConnu]) ; « unite » pour un caractère hors alphabet.
     */
    fun mot(lettre: Char, motConnu: (String) -> Boolean = { true }): String {
        val variantes = VARIANTES[lettre.uppercaseChar()] ?: return "unite"
        return variantes.firstOrNull(motConnu) ?: variantes.first()
    }
}

/**
 * Grammaire fermée Vosk : la liste JSON des phrases autorisées, passée à
 * `Recognizer(modele, 16000f, grammaire)`. Tout ce qui n'y figure pas est capté par [INCONNU],
 * que le parseur de l'application rejette.
 */
object GrammaireFermee {
    /** Jeton Vosk des énoncés hors grammaire. */
    const val INCONNU = "[unk]"

    /** JSON des [phrases] (sans doublon, guillemets retirés), [INCONNU] en dernier. */
    fun json(phrases: Iterable<String>): String =
        (phrases.filter { it.isNotBlank() } + INCONNU).distinct()
            .joinToString(prefix = "[", postfix = "]") { "\"${it.replace("\"", "")}\"" }

    /** Jetons d'un résultat Vosk, sans les [INCONNU] : vide = énoncé rejeté. */
    fun jetons(texte: String): List<String> = texte.trim().split(Regex("\\s+")).filter { it.isNotEmpty() && it != INCONNU }

    /** Vrai si le résultat contient au moins un mot hors grammaire. */
    fun contientInconnu(texte: String): Boolean = texte.split(Regex("\\s+")).any { it == INCONNU }
}
