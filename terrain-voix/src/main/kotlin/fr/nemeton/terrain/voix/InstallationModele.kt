package fr.nemeton.terrain.voix

import java.io.File
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL
import java.security.MessageDigest
import java.util.zip.ZipInputStream

/**
 * Installation d'un modèle Vosk dans [dossier] (stockage privé de l'application), depuis [url]
 * ou depuis une archive déjà sur l'appareil. Le modèle n'est jamais embarqué dans l'APK : tiré une
 * fois (Wi-Fi conseillé, avant la forêt), il sert ensuite hors ligne.
 *
 * Un témoin n'est écrit qu'en fin de décompression : un téléchargement interrompu laisse un dossier
 * sans témoin, donc « non installé ». Bloquant : à appeler hors du fil principal.
 *
 * @param sha256 empreinte attendue de l'archive (hexadécimal), ou null pour ne pas la contrôler.
 */
class InstallationModele(
    val dossier: File,
    private val cache: File,
    val url: String,
    private val sha256: String? = null,
) {
    val estInstalle: Boolean get() = File(dossier, TEMOIN).exists()

    fun supprimer() {
        dossier.deleteRecursively()
    }

    /**
     * Télécharge [url] puis installe. [progression] reçoit un ratio 0..1 (-1 tant que la taille
     * est inconnue) ; [continuer] faux interrompt (annulation). Échec → dossier effacé.
     */
    fun telecharger(progression: (Float) -> Unit = {}, continuer: () -> Boolean = { true }): Result<Unit> {
        val archive = File(cache, "modele-vosk.zip")
        return runCatching {
            archive.delete()
            val connexion = URL(url).openConnection().apply {
                connectTimeout = 20_000
                readTimeout = 30_000
                (this as? HttpURLConnection)?.instanceFollowRedirects = true
            }
            try {
                val total = connexion.contentLengthLong
                connexion.getInputStream().use { entree ->
                    archive.outputStream().use { sortie ->
                        val tampon = ByteArray(64 * 1024)
                        var cumul = 0L
                        while (true) {
                            check(continuer()) { "Téléchargement interrompu" }
                            val lu = entree.read(tampon)
                            if (lu < 0) break
                            sortie.write(tampon, 0, lu)
                            cumul += lu
                            // 95 % de la barre pour le réseau, le reste pour la décompression.
                            progression(if (total > 0) (cumul.toFloat() / total) * 0.95f else -1f)
                        }
                    }
                }
            } finally {
                (connexion as? HttpURLConnection)?.disconnect()
            }
            installer(archive, url).getOrThrow()
            progression(1f)
        }.onFailure { dossier.deleteRecursively() }.also { archive.delete() }
    }

    /**
     * Installe depuis une archive zip locale (modèle importé par fichier) : empreinte contrôlée si
     * [sha256] est donné, dossier racine de l'archive retiré, entrées sortant du dossier refusées.
     * [origine] est noté dans le témoin.
     */
    fun installer(archive: File, origine: String = archive.name): Result<Unit> = runCatching {
        if (sha256 != null) {
            val obtenu = archive.inputStream().use { empreinte(it) }
            require(obtenu.equals(sha256, ignoreCase = true)) { "Empreinte SHA-256 inattendue : $obtenu" }
        }
        dossier.deleteRecursively()
        decompresser(archive)
        File(dossier, TEMOIN).writeText(origine)
    }.onFailure { dossier.deleteRecursively() }

    private fun decompresser(archive: File) {
        dossier.mkdirs()
        val racine = dossier.canonicalFile
        ZipInputStream(archive.inputStream().buffered()).use { zip ->
            while (true) {
                val entree = zip.nextEntry ?: break
                val relatif = entree.name.substringAfter('/', missingDelimiterValue = "")
                if (relatif.isEmpty()) {
                    zip.closeEntry()
                    continue
                }
                val destination = File(dossier, relatif)
                require(destination.canonicalPath.startsWith(racine.path + File.separator)) {
                    "Entrée d'archive hors du dossier cible : ${entree.name}"
                }
                if (entree.isDirectory) {
                    destination.mkdirs()
                } else {
                    destination.parentFile?.mkdirs()
                    destination.outputStream().use { zip.copyTo(it) }
                }
                zip.closeEntry()
            }
        }
    }

    companion object {
        private const val TEMOIN = ".installe"

        /** Empreinte SHA-256 (hexadécimal minuscule) d'un flux. */
        fun empreinte(flux: InputStream): String {
            val md = MessageDigest.getInstance("SHA-256")
            val tampon = ByteArray(64 * 1024)
            while (true) {
                val lu = flux.read(tampon)
                if (lu < 0) break
                md.update(tampon, 0, lu)
            }
            return md.digest().joinToString("") { "%02x".format(it) }
        }
    }
}
