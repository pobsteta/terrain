package fr.nemeton.terrain.journal

/**
 * Fusion de saisies venues de plusieurs appareils, par **union sur la clé** (UUID) avec la règle
 * « dernière écriture gagne » sur l'horodatage `modifie` (ms depuis l'époque Unix) :
 *  - une saisie inconnue localement est ajoutée ;
 *  - une saisie connue n'est remplacée que par une version **strictement** plus récente ; à
 *    égalité, la version locale reste (fusionner deux fois le même fichier ne change rien).
 *
 * Rien n'est jamais supprimé par une fusion : une suppression se transmet comme une saisie
 * (annulation, statut), pas comme une absence.
 */
object Fusion {

    /**
     * Saisies de [entrantes] à écrire localement : inconnues de [modifieLocal] (clé → `modifie`
     * local) ou plus récentes que la version locale. Une même clé présente plusieurs fois dans
     * [entrantes] n'est retenue qu'une fois, dans sa version la plus récente.
     */
    fun <T> aEcrire(
        entrantes: List<T>,
        modifieLocal: Map<String, Long>,
        cle: (T) -> String,
        modifie: (T) -> Long,
    ): List<T> =
        plusRecentes(entrantes, cle, modifie)
            .filter { (modifieLocal[cle(it)] ?: Long.MIN_VALUE) < modifie(it) }

    /** Union de [locales] et [entrantes] : une saisie par clé, la plus récente (la locale à égalité). */
    fun <T> union(
        locales: List<T>,
        entrantes: List<T>,
        cle: (T) -> String,
        modifie: (T) -> Long,
    ): List<T> {
        val resultat = LinkedHashMap<String, T>()
        plusRecentes(locales, cle, modifie).forEach { resultat[cle(it)] = it }
        aEcrire(entrantes, resultat.mapValues { modifie(it.value) }, cle, modifie).forEach { resultat[cle(it)] = it }
        return resultat.values.toList()
    }

    // Première occurrence gardée à égalité, pour que l'ordre d'entrée décide de façon stable.
    private fun <T> plusRecentes(saisies: List<T>, cle: (T) -> String, modifie: (T) -> Long): List<T> {
        val parCle = LinkedHashMap<String, T>()
        saisies.forEach { s ->
            val k = cle(s)
            val deja = parCle[k]
            if (deja == null || modifie(deja) < modifie(s)) parCle[k] = s
        }
        return parCle.values.toList()
    }
}
