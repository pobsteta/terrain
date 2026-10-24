package fr.nemeton.terrain.voix

/**
 * Ce que l'appelant attend de l'archive du modèle : une empreinte à contrôler, ou un refus de
 * contrôler, écrit en toutes lettres. Il n'y a pas de valeur par défaut (ADR 0005) : un oubli
 * d'empreinte ne passe plus en silence.
 */
sealed class Empreinte {
    /**
     * Empreinte SHA-256 attendue : 64 caractères hexadécimaux (FIPS 180-4), majuscules acceptées,
     * normalisée en minuscules dans [hex]. La forme est stricte, sans `trim` : un espace ou un
     * retour à la ligne final lèvent [EmpreinteMalFormee].
     */
    class Sha256(valeur: String) : Empreinte() {
        val hex: String

        init {
            if (!FORME.matches(valeur)) throw EmpreinteMalFormee(valeur)
            hex = valeur.lowercase()
        }

        override fun equals(other: Any?) = other is Sha256 && other.hex == hex
        override fun hashCode() = hex.hashCode()
        override fun toString() = "Sha256($hex)"

        private companion object {
            val FORME = Regex("[0-9a-fA-F]{64}")
        }
    }

    /** L'appelant refuse de contrôler l'empreinte (Alphacephei n'en publie aucune). */
    data object NonControlee : Empreinte()
}
