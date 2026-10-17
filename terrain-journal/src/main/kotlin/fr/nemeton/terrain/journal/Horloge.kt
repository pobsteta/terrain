package fr.nemeton.terrain.journal

import java.util.UUID

/**
 * Horodatage `modifie` **strictement croissant** sur un appareil : deux modifications dans la
 * même milliseconde (ou après un recul de l'horloge système) reçoivent quand même des valeurs
 * distinctes et ordonnées, sans quoi la règle « dernière écriture gagne » ne saurait pas
 * départager. Sûre entre fils d'exécution.
 */
class HorlogeMonotone(private val source: () -> Long = System::currentTimeMillis) {
    private var derniere = Long.MIN_VALUE

    @Synchronized
    fun maintenant(): Long {
        derniere = maxOf(source(), derniere + 1)
        return derniere
    }
}

/** Identifiants des saisies : UUID aléatoire (version 4), en minuscules avec tirets. */
object Identifiants {
    fun nouveau(): String = UUID.randomUUID().toString()

    /** Vrai pour un UUID bien formé (36 caractères, tirets compris), quelle que soit la casse. */
    fun estValide(id: String): Boolean = FORME.matches(id)

    private val FORME = Regex("^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$")
}
