package fr.nemeton.terrain.voix

import java.io.DataInputStream
import java.io.File

/**
 * Vocabulaire du modèle acoustique.
 *
 * Vosk ignore silencieusement les mots d'une grammaire qui ne sont pas dans son lexique (il se
 * contente d'un `Ignoring word missing in vocabulary` dans le journal) : la forme parlée devient
 * alors indictable sans que rien ne le signale à l'opérateur. Plutôt que de promettre des mots au
 * hasard, on lit la table de symboles que le modèle embarque dans `graph/Gr.fst` — un en-tête
 * OpenFst suivi des tables d'entrée et de sortie (135 000 mots pour le modèle small-fr).
 *
 * Seule l'appartenance des mots *candidats* est renvoyée : la table est parcourue en flux, sans
 * jamais matérialiser le lexique complet en mémoire.
 */
object VocabulaireVosk {

    private const val FST_MAGIC = 2125659606
    private const val TABLE_MAGIC = 2125658996

    /** Sous-ensemble de [candidats] réellement connu du modèle ; tout accepter si illisible. */
    fun motsConnus(dossierModele: File, candidats: Set<String>): Set<String> {
        if (candidats.isEmpty()) return emptySet()
        val fst = File(dossierModele, "graph/Gr.fst")
        if (!fst.isFile) return candidats // modèle d'un autre format : on ne bloque rien
        return runCatching {
            DataInputStream(fst.inputStream().buffered(1 shl 16)).use { flux ->
                if (flux.lireInt() != FST_MAGIC) return candidats
                flux.lireChaine() // type de FST
                flux.lireChaine() // type d'arc
                flux.lireInt() // version
                val drapeaux = flux.lireInt()
                flux.lireLong() // propriétés
                flux.lireLong() // état initial
                flux.lireLong() // nombre d'états
                flux.lireLong() // nombre d'arcs
                if (drapeaux and DRAPEAU_SYMBOLES_ENTREE == 0) return candidats
                flux.lireTableSymboles(candidats)
            }
        }.getOrDefault(candidats)
    }

    private const val DRAPEAU_SYMBOLES_ENTREE = 0x1

    /** Table de symboles OpenFst : magie, nom, clé libre, taille, puis (chaîne, clé) × taille. */
    private fun DataInputStream.lireTableSymboles(candidats: Set<String>): Set<String> {
        if (lireInt() != TABLE_MAGIC) return candidats
        lireChaine() // nom de la table
        lireLong() // prochaine clé disponible
        val taille = lireLong()
        val trouves = HashSet<String>(candidats.size)
        var i = 0L
        while (i < taille && trouves.size < candidats.size) {
            val mot = lireChaine()
            lireLong() // clé
            if (mot in candidats) trouves += mot
            i++
        }
        return trouves
    }

    // OpenFst écrit en petit-boutiste ; DataInputStream lit en gros-boutiste.
    private fun DataInputStream.lireInt(): Int = Integer.reverseBytes(readInt())

    private fun DataInputStream.lireLong(): Long = java.lang.Long.reverseBytes(readLong())

    private fun DataInputStream.lireChaine(): String {
        val n = lireInt()
        require(n in 0..MAX_LONGUEUR_MOT) { "Chaîne de symbole invalide : $n" }
        val octets = ByteArray(n)
        readFully(octets)
        return String(octets, Charsets.UTF_8)
    }

    private const val MAX_LONGUEUR_MOT = 4096
}
