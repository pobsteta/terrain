package fr.nemeton.terrain.voix

import java.io.File
import java.net.InetAddress
import java.net.ServerSocket
import java.nio.file.Files
import java.util.concurrent.CyclicBarrier
import java.util.concurrent.atomic.AtomicInteger
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream
import kotlin.concurrent.thread
import kotlin.test.AfterTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertIs
import kotlin.test.assertTrue

/**
 * Tests unitaires de la mission 018, en plus des tests d'acceptation de 017 : états laissés par
 * un processus tué après chaque étape, deux instances en parallèle, `Content-Length` et espace
 * à l'octet près, CRC-32 faux, refus de noms, réseau.
 *
 * Archives : les jeux figés de `src/test/resources/modele/` (Info-ZIP), ou une copie altérée de
 * l'un d'eux ; les zips fabriqués ici (`ZipOutputStream`) ne servent qu'aux refus de structure et
 * de noms, sans valeur de référence calculée par le code testé.
 */
class InstallationModeleSureteTest {
    private val racine: File = Files.createTempDirectory("vosk-surete").toFile()
    private val entrees: File = Files.createTempDirectory("vosk-surete-entrees").toFile()
    private val cible = File(racine, "modele")
    private val nouveau = File(racine, "modele.nouveau")
    private val ancien = File(racine, "modele.ancien")
    private val verrou = File(racine, "modele.verrou")
    private val serveurs = mutableListOf<ServerSocket>()

    @AfterTest
    fun nettoyer() {
        serveurs.forEach { runCatching { it.close() } }
        racine.deleteRecursively()
        entrees.deleteRecursively()
    }

    private fun archive(nom: String): File = File(entrees, nom).also { f ->
        javaClass.getResourceAsStream("/modele/$nom")!!.use { i -> f.outputStream().use { o -> i.copyTo(o) } }
    }

    private fun installation(url: String = "https://exemple/modele.zip", espace: (File) -> Long = { PLENTY }) =
        InstallationModele(cible, url, Empreinte.NonControlee, espaceDisponible = espace)

    private fun dossier(d: File, temoin: Boolean, marque: String) {
        d.mkdirs()
        File(d, "$marque.txt").writeText(marque)
        if (temoin) File(d, TEMOIN).writeText("essai")
    }

    private fun contenu(d: File) = d.list()?.sorted() ?: emptyList()

    private inline fun <reified E : Throwable> erreur(r: Result<*>): E {
        assertTrue(r.isFailure, "un échec était attendu")
        return assertIs<E>(r.exceptionOrNull())
    }

    /** Déclenche la reprise sans rien installer : l'archive n'existe pas. */
    private fun reprise(i: InstallationModele = installation()) =
        erreur<ArchiveIllisible>(i.installer(File(entrees, "absente.zip")))

    private fun zip(nom: String, vararg fichiers: String): File = File(entrees, nom).also { f ->
        ZipOutputStream(f.outputStream()).use { z ->
            fichiers.forEach { z.putNextEntry(ZipEntry(it)); z.write("x\n".toByteArray()); z.closeEntry() }
        }
    }

    private fun modele(prefixe: String) = arrayOf(
        "${prefixe}am/final.mdl", "${prefixe}conf/model.conf", "${prefixe}graph/HCLr.fst",
        "${prefixe}graph/Gr.fst", "${prefixe}graph/disambig_tid.int",
    )

    // ================================================================ processus tué après chaque étape

    @Test
    fun `tue pendant la decompression (etape 7) - nouveau sans temoin - l'ancien reste`() {
        dossier(cible, true, "ancien"); dossier(nouveau, false, "partiel")
        assertTrue(installation().estInstalle)
        reprise()
        assertEquals(listOf(TEMOIN, "ancien.txt"), contenu(cible))
        assertFalse(nouveau.exists())
    }

    @Test
    fun `tue apres le temoin du nouveau (etapes 8 et 9) - l'ancien a temoin l'emporte, le nouveau part`() {
        dossier(cible, true, "ancien"); dossier(nouveau, true, "nouveau")
        reprise()
        assertEquals(listOf(TEMOIN, "ancien.txt"), contenu(cible))
        assertFalse(nouveau.exists())
    }

    @Test
    fun `tue apres le premier renommage (etape 10) - la bascule avance`() {
        dossier(ancien, true, "ancien"); dossier(nouveau, true, "nouveau")
        assertTrue(installation().estInstalle)
        reprise()
        assertEquals(listOf(TEMOIN, "nouveau.txt"), contenu(cible))
        assertFalse(nouveau.exists()); assertFalse(ancien.exists())
    }

    @Test
    fun `tue apres le second renommage (etape 11) - le nouveau reste, l'ancien part`() {
        dossier(cible, true, "nouveau"); dossier(ancien, true, "ancien")
        reprise()
        assertEquals(listOf(TEMOIN, "nouveau.txt"), contenu(cible))
        assertFalse(ancien.exists())
    }

    @Test
    fun `tue pendant l'effacement de l'ancien (etape 12) - temoin deja parti - le nouveau reste`() {
        dossier(cible, true, "nouveau"); dossier(ancien, false, "reste")
        reprise()
        assertEquals(listOf(TEMOIN, "nouveau.txt"), contenu(cible))
        assertFalse(ancien.exists())
    }

    @Test
    fun `tue pendant la reprise R2, dossier a moitie efface - la reprise suivante avance encore`() {
        dossier(cible, false, "reste"); dossier(nouveau, true, "nouveau")
        reprise()
        assertEquals(listOf(TEMOIN, "nouveau.txt"), contenu(cible))
        assertFalse(nouveau.exists())
    }

    @Test
    fun `reprise suivie d'une installation reussie - le nouveau modele remplace celui de la reprise`() {
        dossier(ancien, true, "ancien")
        val i = installation()
        assertTrue(i.installer(archive("bonne.zip")).isSuccess)
        assertTrue(File(cible, "nouveau.txt").isFile)
        assertFalse(File(cible, "ancien.txt").exists())
        assertEquals("bonne.zip", File(cible, TEMOIN).readText(), "le témoin note le nom de l'archive")
        assertFalse(ancien.exists()); assertFalse(nouveau.exists())
    }

    // ================================================================ verrou

    @Test
    fun `deux instances en parallele sur le meme dossier - jamais deux operations a la fois, modele complet a la fin`() {
        val dedans = AtomicInteger(0)
        val auPlus = AtomicInteger(0)
        val espace: (File) -> Long = {
            val n = dedans.incrementAndGet()
            auPlus.accumulateAndGet(n, ::maxOf)
            Thread.sleep(20)
            dedans.decrementAndGet()
            PLENTY
        }
        val archives = listOf(archive("bonne.zip"), archive("bonne-sans-racine.zip"))
        val barriere = CyclicBarrier(4)
        val resultats = java.util.Collections.synchronizedList(mutableListOf<Result<Unit>>())
        val fils = (0 until 4).map { k ->
            thread {
                val i = installation(espace = espace) // une instance par fil
                barriere.await()
                repeat(3) { resultats += i.installer(archives[k % 2]) }
            }
        }
        fils.forEach { it.join() }
        assertEquals(12, resultats.size)
        resultats.forEach { assertTrue(it.isSuccess, "échec : ${it.exceptionOrNull()}") }
        assertEquals(1, auPlus.get(), "deux installations se sont chevauchées")
        assertTrue(File(cible, TEMOIN).isFile)
        listOf("am/final.mdl", "conf/model.conf", "graph/HCLr.fst", "graph/Gr.fst", "graph/disambig_tid.int", "nouveau.txt")
            .forEach { assertTrue(File(cible, it).isFile, "$it absent") }
        assertFalse(nouveau.exists()); assertFalse(ancien.exists())
    }

    @Test
    fun `le verrou n'est jamais efface, meme par supprimer`() {
        val i = installation()
        assertTrue(i.installer(archive("bonne.zip")).isSuccess)
        assertTrue(verrou.isFile)
        assertTrue(i.supprimer().isSuccess)
        assertTrue(verrou.isFile)
        assertEquals(listOf("modele.verrou"), contenu(racine))
    }

    // ================================================================ espace à l'octet près

    @Test
    fun `installer - espace egal a la somme decompressee plus la marge - installee`() {
        val besoin = TAILLE_BONNE + InstallationModele.MARGE_ESPACE
        assertTrue(installation(espace = { besoin }).installer(archive("bonne.zip")).isSuccess)
    }

    @Test
    fun `installer - un octet de moins - EspaceInsuffisant(1), rien n'est cree`() {
        val besoin = TAILLE_BONNE + InstallationModele.MARGE_ESPACE
        val e = erreur<EspaceInsuffisant>(installation(espace = { besoin - 1 }).installer(archive("bonne.zip")))
        assertEquals(1L, e.manquant)
        assertFalse(cible.exists()); assertFalse(nouveau.exists())
    }

    @Test
    fun `marge - 32 Mio`() {
        assertEquals(32L * 1024 * 1024, InstallationModele.MARGE_ESPACE)
    }

    private fun serveur(entete: String, corps: ByteArray): String {
        val s = ServerSocket(0, 5, InetAddress.getByName("127.0.0.1"))
        serveurs += s
        thread(isDaemon = true) {
            while (!s.isClosed) {
                val c = runCatching { s.accept() }.getOrNull() ?: break
                runCatching {
                    c.use {
                        val r = it.getInputStream().bufferedReader()
                        while (true) { val l = r.readLine(); if (l.isNullOrEmpty()) break }
                        it.getOutputStream().apply { write(entete.toByteArray()); write(corps); flush() }
                    }
                }
            }
        }
        return "http://127.0.0.1:${s.localPort}/modele.zip"
    }

    private fun ok(octets: ByteArray) =
        serveur("HTTP/1.1 200 OK\r\nContent-Length: ${octets.size}\r\nConnection: close\r\n\r\n", octets)

    @Test
    fun `telecharger - Content-Length plus marge un octet au-dessus de l'espace - EspaceInsuffisant(1), rien telecharge`() {
        dossier(cible, true, "ancien")
        val octets = archive("bonne.zip").readBytes()
        val libre = octets.size + InstallationModele.MARGE_ESPACE - 1
        val e = erreur<EspaceInsuffisant>(installation(ok(octets), espace = { libre }).telecharger())
        assertEquals(1L, e.manquant)
        assertEquals(listOf(TEMOIN, "ancien.txt"), contenu(cible))
        assertFalse(File(racine, "modele.zip").exists())
    }

    @Test
    fun `telecharger - Content-Length plus marge egal a l'espace - installee (l'archive n'est pas comptee deux fois)`() {
        val octets = archive("bonne.zip").readBytes()
        val libre = octets.size + InstallationModele.MARGE_ESPACE
        assertTrue(installation(ok(octets), espace = { libre }).telecharger().isSuccess)
        assertTrue(File(cible, "nouveau.txt").isFile)
        assertTrue(File(cible, TEMOIN).readText().startsWith("http://127.0.0.1:"), "le témoin note l'URL")
    }

    @Test
    fun `telecharger - erreur HTTP 404 - ArchiveIllisible reseau, l'ancien est intact`() {
        dossier(cible, true, "ancien")
        val url = serveur("HTTP/1.1 404 Not Found\r\nContent-Length: 0\r\nConnection: close\r\n\r\n", ByteArray(0))
        val e = erreur<ArchiveIllisible>(installation(url).telecharger())
        assertTrue(e.raison.startsWith("réseau"), e.raison)
        assertEquals(listOf(TEMOIN, "ancien.txt"), contenu(cible))
    }

    @Test
    fun `telecharger - serveur injoignable et URL fichier absente - ArchiveIllisible reseau`() {
        val port = ServerSocket(0).use { it.localPort }
        val e1 = erreur<ArchiveIllisible>(installation("http://127.0.0.1:$port/m.zip").telecharger())
        assertTrue(e1.raison.startsWith("réseau"), e1.raison)
        val e2 = erreur<ArchiveIllisible>(installation(File(entrees, "absente.zip").toURI().toString()).telecharger())
        assertTrue(e2.raison.startsWith("réseau"), e2.raison)
    }

    @Test
    fun `telecharger - flux plus long que le Content-Length annonce - ArchiveIllisible`() {
        dossier(cible, true, "ancien")
        val octets = archive("bonne.zip").readBytes()
        // HttpURLConnection s'arrête au Content-Length : un flux annoncé court est lu tronqué, donc refusé.
        val url = serveur("HTTP/1.1 200 OK\r\nContent-Length: ${octets.size / 2}\r\nConnection: close\r\n\r\n", octets)
        erreur<ArchiveIllisible>(installation(url).telecharger())
        assertEquals(listOf(TEMOIN, "ancien.txt"), contenu(cible))
    }

    // ================================================================ CRC-32 et noms

    @Test
    fun `CRC-32 faux - ArchiveIllisible, l'ancien est intact, nouveau absent`() {
        dossier(cible, true, "ancien")
        // bonne.zip est « stockée » (zip -0) : le contenu de am/final.mdl (« final\n ») y est en clair.
        val octets = archive("bonne.zip").readBytes()
        val texte = String(octets, Charsets.ISO_8859_1)
        val k = texte.indexOf("final\n")
        assertTrue(k >= 0)
        "FINAL\n".toByteArray().copyInto(octets, k)
        val alteree = File(entrees, "crc-faux.zip").also { it.writeBytes(octets) }
        val e = erreur<ArchiveIllisible>(installation().installer(alteree))
        assertTrue(e.raison.contains("CRC"), e.raison)
        assertEquals(listOf(TEMOIN, "ancien.txt"), contenu(cible))
        assertFalse(nouveau.exists())
    }

    @Test
    fun `deux racines candidates a la meme profondeur - ArchiveIllisible plusieurs modeles`() {
        val a = zip("deux.zip", *modele("m1/"), *modele("m2/"))
        val e = erreur<ArchiveIllisible>(installation().installer(a))
        assertTrue(e.raison.startsWith("plusieurs modèles"), e.raison)
    }

    @Test
    fun `modele imbrique - la racine la plus haute est retenue`() {
        val a = zip("imbrique.zip", *modele("m/"), *modele("m/sous/"))
        assertTrue(installation().installer(a).isSuccess)
        assertTrue(File(cible, "am/final.mdl").isFile)
        assertTrue(File(cible, "sous/am/final.mdl").isFile)
    }

    @Test
    fun `nom absolu - ArchiveIllisible`() {
        val a = zip("absolu.zip", *modele("m/"), "/tmp/absolu-018.txt")
        erreur<ArchiveIllisible>(installation().installer(a))
        assertFalse(File("/tmp/absolu-018.txt").exists())
    }

    @Test
    fun `segment point-point separe par une barre oblique inverse - ArchiveIllisible`() {
        val a = zip("inverse.zip", *modele("m/"), "m\\..\\..\\inverse-018.txt")
        erreur<ArchiveIllisible>(installation().installer(a))
        assertFalse(File(racine, "inverse-018.txt").exists())
    }

    @Test
    fun `entree hors de la racine - non installee`() {
        val a = zip("hors.zip", *modele("m/"), "LISEZMOI.txt")
        assertTrue(installation().installer(a).isSuccess)
        assertFalse(File(cible, "LISEZMOI.txt").exists())
        assertFalse(File(racine, "LISEZMOI.txt").exists())
    }

    @Test
    fun `archive absente - ArchiveIllisible`() {
        reprise()
    }

    // ================================================================ supprimer, Empreinte

    @Test
    fun `supprimer efface chaque dossier, temoin compris`() {
        dossier(cible, true, "a"); dossier(nouveau, true, "b"); dossier(ancien, false, "c")
        val i = installation()
        assertTrue(i.supprimer().isSuccess)
        assertFalse(cible.exists()); assertFalse(nouveau.exists()); assertFalse(ancien.exists())
        assertFalse(i.estInstalle)
    }

    @Test
    fun `Empreinte Sha256 - normalisee en minuscules, egale quelle que soit la casse`() {
        val hex = "0123456789abcdef".repeat(4)
        assertEquals(hex, Empreinte.Sha256(hex.uppercase()).hex)
        assertEquals(Empreinte.Sha256(hex), Empreinte.Sha256(hex.uppercase()))
        assertIs<EmpreinteMalFormee>(runCatching { Empreinte.Sha256(" $hex") }.exceptionOrNull())
    }

    private companion object {
        const val TEMOIN = ".installe"
        /** 6 + 5 + 5 + 3 + 9 + 8 octets décompressés dans bonne.zip (fabriquer.sh). */
        const val TAILLE_BONNE = 36L
        const val PLENTY = 1L shl 40
    }
}
