package fr.nemeton.terrain.voix

import java.io.EOFException
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URI
import java.nio.channels.FileChannel
import java.nio.file.Files
import java.nio.file.StandardOpenOption
import java.security.MessageDigest
import java.util.concurrent.ConcurrentHashMap
import java.util.zip.CRC32
import java.util.zip.ZipEntry
import java.util.zip.ZipException
import java.util.zip.ZipFile

/**
 * Installation d'un modèle Vosk dans [dossier] (stockage privé de l'application), depuis [url]
 * ou depuis une archive déjà sur l'appareil. Le modèle n'est jamais embarqué dans l'APK : tiré une
 * fois (Wi-Fi conseillé, avant la forêt), il sert ensuite hors ligne.
 *
 * **Le modèle en place survit à tout échec** (ADR 0005, spec de `terrain-voix`) : le nouveau est
 * décompressé dans `<nom>.nouveau`, à côté de [dossier], vérifié, écrit sur le disque, puis marqué
 * d'un témoin ; la bascule se fait ensuite en deux renommages (l'ancien vers `<nom>.ancien`, le
 * nouveau vers [dossier]). Une archive illisible, une empreinte fausse, un disque plein, une coupure
 * réseau ou une annulation laissent [dossier] intact. Si le processus est tué entre deux étapes,
 * la reprise (R1 à R4), appliquée sous verrou au début de [installer], [telecharger], [supprimer]
 * et [reprendre], remet un état sûr : elle avance si le nouveau est prêt, sinon elle remet l'ancien.
 * Le seul témoin est celui que pose l'installation (étape 8) : une entrée `.installe` de l'archive
 * est ignorée, jamais décompressée.
 *
 * **Avant de charger le modèle**, l'application appelle [reprendre] hors du fil principal : une
 * bascule interrompue (états R2 et R3) est ainsi remise en [dossier], là où Vosk le cherche.
 *
 * Dans le dossier parent, à côté de [dossier] : `<nom>.nouveau`, `<nom>.ancien`, `<nom>.zip`
 * (archive téléchargée) et `<nom>.verrou` (jamais effacé). Le constructeur n'écrit rien.
 *
 * Bloquant : [installer], [telecharger], [supprimer] et [reprendre] s'appellent hors du fil
 * principal ; [estInstalle] est une lecture pure. Le verrou est partagé par tout le processus :
 * un second appel depuis un autre fil attend la fin du premier. Un appel imbriqué, fait depuis
 * `progression` ou `continuer` (même fil, verrou déjà tenu), n'attend pas : il obtient un
 * `java.nio.channels.OverlappingFileLockException` dans son `Result`, sans rien toucher.
 *
 * @param empreinte empreinte attendue de l'archive, ou [Empreinte.NonControlee] : un choix
 *   explicite de l'appelant, sans valeur par défaut.
 * @param espaceDisponible octets libres d'un dossier (le parent de [dossier]) ; sur Android,
 *   l'application peut passer `StorageManager.getAllocatableBytes`.
 */
class InstallationModele(
    val dossier: File,
    val url: String,
    val empreinte: Empreinte,
    private val espaceDisponible: (File) -> Long = File::getUsableSpace,
) {
    private val parent: File = dossier.absoluteFile.parentFile
        ?: throw IllegalArgumentException("Le dossier du modèle doit avoir un parent : $dossier")
    private val nouveau = File(parent, "${dossier.name}.nouveau")
    private val ancien = File(parent, "${dossier.name}.ancien")
    private val archiveTelechargee = File(parent, "${dossier.name}.zip")
    private val fichierVerrou = File(parent, "${dossier.name}.verrou")

    /**
     * Vrai si un modèle complet est sur le disque : dans [dossier], ou récupérable par la reprise
     * (bascule interrompue). Lecture pure : n'écrit jamais, sûre sur le fil principal.
     */
    val estInstalle: Boolean
        get() = aTemoin(dossier) || aTemoin(nouveau) || aTemoin(ancien)

    /**
     * Prend le verrou et applique la reprise R1 à R4, sans rien installer : un modèle à témoin
     * laissé par une bascule interrompue est remis en [dossier], tout dossier sans témoin est
     * effacé (témoin d'abord). À appeler hors du fil principal avant de charger le modèle. Un
     * échec d'entrée-sortie est porté par le `Result`.
     */
    fun reprendre(): Result<Unit> = runCatching { sousVerrou { } }

    /**
     * Efface le modèle ([dossier], `<nom>.nouveau`, `<nom>.ancien`, chacun témoin en premier) et
     * l'archive téléchargée. Un échec d'entrée-sortie est porté par le `Result`.
     */
    fun supprimer(): Result<Unit> = runCatching {
        sousVerrou {
            invalider(dossier)
            invalider(nouveau)
            invalider(ancien)
            Files.deleteIfExists(archiveTelechargee.toPath())
        }
    }

    /**
     * Télécharge [url] dans `<nom>.zip` puis installe comme [installer]. [progression] reçoit un
     * ratio 0..1 (-1 tant que la taille est inconnue) et finit à 1 ; [continuer] est consulté à
     * chaque bloc : faux, il interrompt ([Interrompu]). Si `Content-Length` est connu, l'espace
     * libre est contrôlé avant le téléchargement ([EspaceInsuffisant]). Une coupure ou une erreur
     * du réseau donnent [ArchiveIllisible] (« réseau : … »).
     *
     * **Sur tout échec, [dossier] est intact**, et `<nom>.zip` est effacé dans tous les cas.
     */
    fun telecharger(progression: (Float) -> Unit = {}, continuer: () -> Boolean = { true }): Result<Unit> =
        runCatching {
            sousVerrou {
                try {
                    Files.deleteIfExists(archiveTelechargee.toPath())
                    telechargerArchive(progression, continuer)
                    installerSousVerrou(archiveTelechargee, url, continuer)
                } finally {
                    runCatching { Files.deleteIfExists(archiveTelechargee.toPath()) }
                }
            }
            progression(1f)
        }

    /**
     * Installe depuis une archive zip locale (modèle importé par fichier). Dans l'ordre, le premier
     * contrôle qui échoue donne son erreur : archive lisible ([ArchiveIllisible]), entrée utile
     * ([StructureInvalide]), empreinte ([EmpreinteInattendue]), structure du modèle sous sa racine
     * ([StructureInvalide], [SansGrammaireDynamique]), espace libre ([EspaceInsuffisant]), puis
     * décompression sûre (noms, tailles, CRC-32 : [ArchiveIllisible] ; [continuer] faux :
     * [Interrompu]). Le nom de l'archive est noté dans le témoin. Une entrée `.installe` sous la
     * racine de l'archive est ignorée. Si seul l'effacement final de `<nom>.ancien` échoue, le
     * nouveau modèle est en place et le résultat est un succès : la reprise suivante l'effacera.
     *
     * **Sur tout échec, [dossier] est intact.**
     */
    fun installer(archive: File, continuer: () -> Boolean = { true }): Result<Unit> = runCatching {
        sousVerrou { installerSousVerrou(archive, archive.name, continuer) }
    }

    // ------------------------------------------------------------------ verrou et reprise

    /** Verrou du processus (par chemin canonique), puis `FileChannel.lock`, puis reprise. */
    private fun <T> sousVerrou(action: () -> T): T {
        Files.createDirectories(parent.toPath())
        val moniteur = VERROUS.computeIfAbsent(fichierVerrou.canonicalPath) { Any() }
        return synchronized(moniteur) {
            FileChannel.open(fichierVerrou.toPath(), StandardOpenOption.CREATE, StandardOpenOption.WRITE).use { canal ->
                canal.lock().use {
                    appliquerReprise()
                    action()
                }
            }
        }
    }

    /** Table d'états R1 à R4 de la spec : la première règle qui s'applique l'emporte. */
    private fun appliquerReprise() {
        when {
            aTemoin(dossier) -> { // R1
                invalider(nouveau)
                invalider(ancien)
            }
            aTemoin(nouveau) -> { // R2 : avancer
                invalider(dossier)
                renommer(nouveau, dossier)
                invalider(ancien)
            }
            aTemoin(ancien) -> { // R3 : reculer
                invalider(dossier)
                invalider(nouveau)
                renommer(ancien, dossier)
            }
            else -> { // R4
                invalider(dossier)
                invalider(nouveau)
                invalider(ancien)
            }
        }
    }

    // ------------------------------------------------------------------ installation

    private fun installerSousVerrou(archive: File, origine: String, continuer: () -> Boolean) {
        // 2. Ouvrir l'archive par son répertoire central.
        val zip = try {
            ZipFile(archive)
        } catch (e: IOException) {
            throw ArchiveIllisible("pas un zip lisible (${e.message ?: e.javaClass.simpleName})")
        } catch (e: IllegalArgumentException) { // nom d'entrée qui n'est pas en UTF-8
            throw ArchiveIllisible("nom d'entrée illisible (${e.message})")
        }
        zip.use { z ->
            val entrees = lireZip { z.entries().toList() }
            val utiles = entrees.filter { !it.isDirectory && !ignoree(it.name) }
            if (utiles.isEmpty()) throw StructureInvalide(OBLIGATOIRES.first())

            // 3. Empreinte de l'archive.
            val attendue = empreinte
            if (attendue is Empreinte.Sha256) {
                val obtenue = archive.inputStream().use { Companion.empreinte(it) }
                if (obtenue != attendue.hex) throw EmpreinteInattendue(attendue.hex, obtenue)
            }

            // 4. Racine, puis structure.
            val racine = racine(utiles.map { it.name }.toSet())
            // Une entrée `.installe` de l'archive n'est jamais décompressée : le seul témoin est
            // celui de l'étape 8 (sinon une coupure en pleine décompression ferait avancer R2).
            val sousRacine = entrees.filter {
                !ignoree(it.name) && it.name.startsWith(racine) && it.name != racine &&
                    !estTemoin(it.name.removePrefix(racine))
            }

            // 5. Somme des tailles décompressées, sous la racine.
            var somme = 0L
            for (e in sousRacine) {
                if (e.isDirectory) continue
                if (e.size < 0) throw ArchiveIllisible("taille inconnue : ${e.name}")
                somme = sansDebordement { Math.addExact(somme, e.size) }
            }

            // 6. Espace libre : rien n'est encore écrit, rien ne sera effacé.
            verifierEspace(somme)

            // 7 à 12.
            verifierNoms(entrees)
            try {
                decompresser(z, sousRacine, racine, continuer)
                basculer(origine)
            } catch (e: Throwable) {
                try {
                    invalider(nouveau)
                } catch (suite: Throwable) {
                    e.addSuppressed(suite)
                }
                throw e
            }
        }
    }

    /** Dossier le plus haut qui contient `am/final.mdl` et `conf/model.conf`, contrôlé. */
    private fun racine(fichiers: Set<String>): String {
        fun prefixes(suffixe: String) = fichiers.filter { it.endsWith(suffixe) }
            .map { it.removeSuffix(suffixe) }
            .filter { it.isEmpty() || it.endsWith("/") }
        fun profondeur(p: String) = p.count { it == '/' }

        val completes = prefixes(OBLIGATOIRES[0]).filter { (it + OBLIGATOIRES[1]) in fichiers }
        val racine = if (completes.isNotEmpty()) {
            val haut = completes.minOf(::profondeur)
            val auPlusHaut = completes.filter { profondeur(it) == haut }
            if (auPlusHaut.size > 1) throw ArchiveIllisible("plusieurs modèles : ${auPlusHaut.sorted()}")
            auPlusHaut.single()
        } else {
            // Modèle incomplet : on désigne le premier fichier obligatoire absent du modèle partiel.
            (prefixes(OBLIGATOIRES[0]) + prefixes(OBLIGATOIRES[1]))
                .sortedWith(compareBy(::profondeur).thenBy { it })
                .firstOrNull()
                ?: throw StructureInvalide(OBLIGATOIRES.first())
        }
        if (racine + HCLG in fichiers) throw SansGrammaireDynamique()
        OBLIGATOIRES.firstOrNull { racine + it !in fichiers }?.let { throw StructureInvalide(it) }
        return racine
    }

    private fun verifierEspace(octets: Long) {
        val besoin = sansDebordement { Math.addExact(octets, MARGE_ESPACE) }
        val libre = espaceDisponible(parent)
        if (libre < besoin) throw EspaceInsuffisant(besoin - maxOf(libre, 0L))
    }

    /** Refus de l'ADR 0005 et de la spec : nom absolu, segment `..` (par `/` ou `\`), doublon. */
    private fun verifierNoms(entrees: List<ZipEntry>) {
        val vus = HashSet<String>()
        for (e in entrees) {
            val nom = e.name
            if (nom.startsWith("/") || nom.startsWith("\\") || LECTEUR.containsMatchIn(nom)) {
                throw ArchiveIllisible("nom d'entrée absolu : $nom")
            }
            if (nom.split('/', '\\').any { it == ".." }) throw ArchiveIllisible("entrée à segment « .. » : $nom")
            if (!vus.add(nom)) throw ArchiveIllisible("entrée en double : $nom")
        }
    }

    /** 7 et 8 : décompresse sous la racine dans `<nom>.nouveau`, écrit sur le disque, pose le témoin. */
    private fun decompresser(z: ZipFile, sousRacine: List<ZipEntry>, racine: String, continuer: () -> Boolean) {
        invalider(nouveau)
        Files.createDirectories(nouveau.toPath())
        val base = nouveau.canonicalPath + File.separator
        val tampon = ByteArray(64 * 1024)
        for (e in sousRacine) {
            if (!continuer()) throw Interrompu()
            val destination = File(nouveau, e.name.removePrefix(racine))
            if (!destination.canonicalPath.startsWith(base)) throw ArchiveIllisible("entrée hors du dossier : ${e.name}")
            if (e.isDirectory) {
                Files.createDirectories(destination.toPath())
                continue
            }
            Files.createDirectories(destination.parentFile.toPath())
            lireZip { z.getInputStream(e) }.use { flux ->
                FileOutputStream(destination).use { sortie ->
                    val crc = CRC32()
                    var ecrits = 0L
                    while (true) {
                        if (!continuer()) throw Interrompu()
                        val lu = lireZip { flux.read(tampon) }
                        if (lu < 0) break
                        ecrits += lu
                        if (ecrits > e.size) throw ArchiveIllisible("plus d'octets que déclaré : ${e.name}")
                        crc.update(tampon, 0, lu)
                        sortie.write(tampon, 0, lu)
                    }
                    if (ecrits != e.size) throw ArchiveIllisible("moins d'octets que déclaré : ${e.name}")
                    if (e.crc != -1L && crc.value != e.crc) throw ArchiveIllisible("CRC-32 faux : ${e.name}")
                    sortie.channel.force(true)
                }
            }
        }
        if (!continuer()) throw Interrompu()
        nouveau.walkBottomUp().filter { it.isDirectory }.forEach(::forcerDossier)
    }

    /** 8 (témoin) à 12 : `<nom>.nouveau` est complet sur le disque ; on bascule. */
    private fun basculer(origine: String) {
        FileOutputStream(File(nouveau, TEMOIN)).use { sortie ->
            sortie.write(origine.toByteArray(Charsets.UTF_8))
            sortie.channel.force(true)
        }
        forcerDossier(nouveau)
        invalider(ancien) // 9
        if (dossier.exists()) renommer(dossier, ancien) // 10
        try {
            renommer(nouveau, dossier) // 11
        } catch (e: Throwable) {
            try {
                if (!dossier.exists() && ancien.exists()) renommer(ancien, dossier)
            } catch (suite: Throwable) {
                e.addSuppressed(suite)
            }
            throw e
        }
        // 12 : le nouveau est en place ; un échec ici n'est pas un échec de l'installation,
        // la reprise R1 effacera l'ancien à l'opération suivante.
        try {
            invalider(ancien)
        } catch (_: Exception) {
            // Effacement refusé (droits, entrée-sortie) : l'installation a réussi quand même.
        }
    }

    // ------------------------------------------------------------------ téléchargement

    private fun telechargerArchive(progression: (Float) -> Unit, continuer: () -> Boolean) {
        val adresse = try {
            URI(url).toURL()
        } catch (e: Exception) { // URISyntaxException, IllegalArgumentException, MalformedURLException
            throw ArchiveIllisible("réseau : URL invalide ($url)")
        }
        val connexion = reseau {
            adresse.openConnection().apply {
                connectTimeout = 20_000
                readTimeout = 30_000
                (this as? HttpURLConnection)?.instanceFollowRedirects = true
            }
        }
        try {
            val total = reseau {
                connexion.connect()
                if (connexion is HttpURLConnection) {
                    val code = connexion.responseCode
                    if (code !in 200..299) throw IOException("HTTP $code")
                }
                connexion.contentLengthLong
            }
            if (total >= 0) verifierEspace(total)
            var cumul = 0L
            reseau { connexion.getInputStream() }.use { entree ->
                FileOutputStream(archiveTelechargee).use { sortie ->
                    val tampon = ByteArray(64 * 1024)
                    while (true) {
                        if (!continuer()) throw Interrompu()
                        val lu = reseau { entree.read(tampon) }
                        if (lu < 0) break
                        cumul += lu
                        if (total >= 0 && cumul > total) throw ArchiveIllisible("réseau : plus d'octets qu'annoncé")
                        sortie.write(tampon, 0, lu)
                        // 95 % de la barre pour le réseau, le reste pour la décompression.
                        progression(if (total > 0) (cumul.toFloat() / total) * 0.95f else -1f)
                    }
                }
            }
            if (total >= 0 && cumul != total) throw ArchiveIllisible("réseau : flux tronqué ($cumul octets sur $total)")
        } finally {
            (connexion as? HttpURLConnection)?.disconnect()
        }
    }

    // ------------------------------------------------------------------ disque

    private fun aTemoin(d: File) = File(d, TEMOIN).isFile

    /** `.installe` (fichier ou dossier) au sommet du modèle, en nom relatif à la racine. */
    private fun estTemoin(relatif: String) = relatif.trimEnd('/').substringBefore('/') == TEMOIN

    /**
     * Invalide puis efface un dossier de modèle : **le témoin d'abord**, écrit sur le disque, puis
     * le reste. Un effacement interrompu ne laisse donc jamais un dossier à témoin incomplet.
     */
    private fun invalider(d: File) {
        if (!d.exists()) return
        if (d.isDirectory) {
            Files.deleteIfExists(File(d, TEMOIN).toPath())
            forcerDossier(d)
        }
        if (!d.deleteRecursively()) throw IOException("Effacement impossible : $d")
        forcerDossier(parent)
    }

    /** Renommage dans le même dossier parent (`Files.move`, sans `REPLACE_EXISTING`). */
    private fun renommer(de: File, vers: File) {
        Files.move(de.toPath(), vers.toPath())
        forcerDossier(parent)
    }

    companion object {
        private const val TEMOIN = ".installe"

        /**
         * Marge d'espace libre exigée au-delà du besoin (32 Mio, fixée par Pascal le 2026-10-24) :
         * la même pour [installer] (taille décompressée + marge) et [telecharger]
         * (`Content-Length` + marge avant le téléchargement).
         */
        const val MARGE_ESPACE: Long = 33_554_432L

        /** Fichiers obligatoires d'un modèle à grammaire dynamique, dans l'ordre du contrôle. */
        private val OBLIGATOIRES = listOf(
            "am/final.mdl",
            "conf/model.conf",
            "graph/HCLr.fst",
            "graph/Gr.fst",
            "graph/disambig_tid.int",
        )
        private const val HCLG = "graph/HCLG.fst"
        private val LECTEUR = Regex("^[A-Za-z]:")

        /** Verrous du processus, rangés par chemin canonique de `<nom>.verrou`. */
        private val VERROUS = ConcurrentHashMap<String, Any>()

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

        private fun ignoree(nom: String): Boolean =
            nom.substringBefore('/') == "__MACOSX" || nom.trimEnd('/').substringAfterLast('/') == ".DS_Store"

        /** Lecture de l'archive : une faute du zip devient [ArchiveIllisible] ; l'écriture n'y passe pas. */
        private inline fun <T> lireZip(lecture: () -> T): T = try {
            lecture()
        } catch (e: ZipException) {
            throw ArchiveIllisible("zip invalide (${e.message})")
        } catch (e: EOFException) {
            throw ArchiveIllisible("zip tronqué (${e.message})")
        } catch (e: IllegalArgumentException) { // ZipCoder : nom d'entrée qui n'est pas en UTF-8
            throw ArchiveIllisible("nom d'entrée illisible (${e.message})")
        }

        /** Lecture du réseau : une faute d'entrée-sortie devient [ArchiveIllisible] (« réseau : … »). */
        private inline fun <T> reseau(lecture: () -> T): T = try {
            lecture()
        } catch (e: IOException) {
            throw ArchiveIllisible("réseau : ${e.message ?: e.javaClass.simpleName}")
        }

        private inline fun sansDebordement(somme: () -> Long): Long = try {
            somme()
        } catch (_: ArithmeticException) {
            throw ArchiveIllisible("somme des tailles qui déborde")
        }

        /** Écriture forcée d'un dossier (ses entrées) ; sans effet là où le système ne le permet pas. */
        private fun forcerDossier(d: File) {
            try {
                FileChannel.open(d.toPath(), StandardOpenOption.READ).use { it.force(true) }
            } catch (_: IOException) {
                // Windows refuse d'ouvrir un dossier : l'écriture des fichiers reste forcée.
            }
        }
    }
}
