package fr.nemeton.terrain.voix

import java.io.File
import java.io.OutputStream
import java.net.ServerSocket
import java.nio.file.Files
import java.security.MessageDigest
import kotlin.concurrent.thread
import kotlin.test.AfterTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertIs
import kotlin.test.assertTrue

/**
 * Tests d'acceptation de l'installation du modèle (mission 017, spec de `terrain-voix`,
 * critères 1 à 8, ADR 0005). Écrits sans lire l'implémentation.
 *
 * Les archives viennent de `src/test/resources/modele/` (fabriquées par `fabriquer.sh`, Info-ZIP) ;
 * leurs empreintes de référence sont celles de `empreintes.txt` (sha256sum), jamais calculées par
 * le code testé.
 *
 * Noms supposés d'après la spec, à confirmer par la mission 018 (voir le rendu de la mission) :
 * erreurs typées de premier niveau dans `fr.nemeton.terrain.voix`, champs `valeur`, `attendue`,
 * `obtenue`, `raison`, `manquant` ; `manquant` d'`EspaceInsuffisant` numérique (Long) ;
 * `installer(archive, continuer)` ; `Empreinte.Sha256(hex)` qui lève `EmpreinteMalFormee`.
 */
class InstallationModeleTest {
    private val racine: File = Files.createTempDirectory("vosk").toFile()
    private val entrees: File = Files.createTempDirectory("vosk-entrees").toFile()
    private val cible = File(racine, "modele")
    private val nouveau = File(racine, "modele.nouveau")
    private val ancien = File(racine, "modele.ancien")
    private val zipTemporaire = File(racine, "modele.zip")
    private val serveurs = mutableListOf<ServerSocket>()

    @AfterTest
    fun nettoyer() {
        serveurs.forEach { runCatching { it.close() } }
        racine.deleteRecursively()
        entrees.deleteRecursively()
    }

    // ---------------------------------------------------------------- jeux d'essai

    /** Empreintes de référence : `empreintes.txt`, écrit par sha256sum. */
    private val reference: Map<String, String> by lazy {
        val txt = javaClass.getResourceAsStream("/modele/empreintes.txt")!!.use { it.readBytes().toString(Charsets.UTF_8) }
        txt.lines().filter { it.isNotBlank() }.associate { l -> l.substringAfter("  ") to l.substringBefore("  ") }
    }

    private fun archive(nom: String): File = File(entrees, nom).also { f ->
        javaClass.getResourceAsStream("/modele/$nom")!!.use { i -> f.outputStream().use { o -> i.copyTo(o) } }
        // Garde-fou : le jeu d'essai lu est bien celui que sha256sum a figé.
        val lu = MessageDigest.getInstance("SHA-256").digest(f.readBytes()).joinToString("") { "%02x".format(it) }
        assertEquals(reference.getValue(nom), lu, "le jeu d'essai $nom ne correspond plus à empreintes.txt")
    }

    private fun sha(nom: String) = Empreinte.Sha256(reference.getValue(nom))

    private fun installation(
        empreinte: Empreinte = Empreinte.NonControlee,
        url: String = "https://exemple/modele.zip",
        espace: (File) -> Long = { PLENTY },
    ) = InstallationModele(cible, url, empreinte, espaceDisponible = espace)

    // ---------------------------------------------------------------- états sur le disque

    private fun dossierAvec(d: File, temoin: Boolean, vararg fichiers: Pair<String, String>) {
        d.mkdirs()
        fichiers.forEach { (nom, contenu) -> File(d, nom).also { it.parentFile.mkdirs() }.writeText(contenu) }
        if (temoin) File(d, TEMOIN).writeText("origine d'essai")
    }

    /** Le modèle du forestier : installé, avec témoin, et reconnaissable à `ancien.txt`. */
    private fun poserAncien(d: File = cible) =
        dossierAvec(d, true, "ancien.txt" to "ancien", "am/final.mdl" to "ancien-mdl")

    /** Un modèle prêt mais pas encore basculé : reconnaissable à `nouveau.txt`. */
    private fun poserNouveauPret(d: File = nouveau) =
        dossierAvec(d, true, "nouveau.txt" to "nouveau", "am/final.mdl" to "nouveau-mdl")

    private fun poserPartiel(d: File) = dossierAvec(d, false, "partiel.txt" to "partiel")

    private fun assertAncienIntact(message: String = "") {
        assertTrue(File(cible, "ancien.txt").isFile, "$message : ancien.txt a disparu")
        assertEquals("ancien", File(cible, "ancien.txt").readText(), message)
        assertEquals("ancien-mdl", File(cible, "am/final.mdl").readText(), message)
        assertTrue(File(cible, TEMOIN).isFile, "$message : témoin perdu")
        assertFalse(File(cible, "nouveau.txt").exists(), "$message : du nouveau a fuité dans l'ancien")
        assertFalse(nouveau.exists(), "$message : .nouveau traîne")
        assertFalse(ancien.exists(), "$message : .ancien traîne")
    }

    private fun assertNouveauInstalle(message: String = "") {
        assertTrue(File(cible, "nouveau.txt").isFile, "$message : nouveau.txt absent")
        assertTrue(File(cible, "am/final.mdl").isFile, message)
        assertTrue(File(cible, TEMOIN).isFile, "$message : témoin absent")
        assertFalse(File(cible, "ancien.txt").exists(), "$message : l'ancien survit dans le nouveau")
        assertFalse(File(cible, "vosk-model-test").exists(), "$message : le dossier de tête n'a pas été retiré")
        assertFalse(nouveau.exists(), "$message : .nouveau traîne")
        assertFalse(ancien.exists(), "$message : .ancien traîne")
    }

    /** Photo de l'arborescence : chemin, nature, taille et contenu. */
    private fun photo(dir: File): Map<String, String> =
        dir.walkTopDown().associate { f ->
            f.relativeTo(dir).path to if (f.isDirectory) "dossier" else "fichier:${f.length()}:${f.readText()}"
        }

    private inline fun <reified E : Throwable> erreur(r: Result<*>): E {
        val e = r.exceptionOrNull()
        assertTrue(r.isFailure, "un échec était attendu")
        assertIs<E>(e)
        return e
    }

    // ---------------------------------------------------------------- serveur HTTP local

    /** Sert [corps] avec un `Content-Length` annoncé de [longueur] (on peut mentir), puis ferme. */
    private fun serveur(longueur: Long, corps: ByteArray): String {
        val s = ServerSocket(0, 5, java.net.InetAddress.getByName("127.0.0.1"))
        serveurs += s
        thread(isDaemon = true) {
            while (!s.isClosed) {
                val c = runCatching { s.accept() }.getOrNull() ?: break
                runCatching {
                    c.use {
                        val entree = it.getInputStream().bufferedReader()
                        val requete = entree.readLine() ?: ""
                        while (true) { val l = entree.readLine(); if (l.isNullOrEmpty()) break }
                        val o: OutputStream = it.getOutputStream()
                        o.write("HTTP/1.1 200 OK\r\nContent-Length: $longueur\r\nConnection: close\r\n\r\n".toByteArray())
                        if (!requete.startsWith("HEAD")) o.write(corps)
                        o.flush()
                    }
                }
            }
        }
        return "http://127.0.0.1:${s.localPort}/modele.zip"
    }

    private fun serveurComplet(nom: String): String {
        val octets = archive(nom).readBytes()
        return serveur(octets.size.toLong(), octets)
    }

    // ================================================================ critère 1 : table d'états

    @Test
    fun `critere 1 - R1 modele installe et debris - le forestier garde son modele, les debris partent`() {
        poserAncien(); poserPartiel(nouveau); poserPartiel(ancien)
        val i = installation()
        assertTrue(i.estInstalle)
        erreur<ArchiveIllisible>(i.installer(archive("tronquee.zip")))
        assertAncienIntact("R1")
    }

    @Test
    fun `critere 1 - R1 l'emporte sur R2 et R3 - dossier, nouveau et ancien tous a temoin, le dossier reste`() {
        poserAncien(); poserNouveauPret(); poserAncien(ancien)
        File(ancien, "ancien.txt").writeText("ancien-bis")
        val i = installation()
        assertTrue(i.estInstalle)
        erreur<ArchiveIllisible>(i.installer(archive("tronquee.zip")))
        assertAncienIntact("R1 avant R2")
    }

    @Test
    fun `critere 1 - R2 dossier absent, nouveau pret - la bascule est menee a terme`() {
        poserNouveauPret(); poserAncien(ancien)
        val i = installation()
        assertTrue(i.estInstalle)
        erreur<ArchiveIllisible>(i.installer(archive("tronquee.zip")))
        assertTrue(File(cible, "nouveau.txt").isFile)
        assertEquals("nouveau-mdl", File(cible, "am/final.mdl").readText())
        assertTrue(File(cible, TEMOIN).isFile)
        assertFalse(File(cible, "ancien.txt").exists())
        assertFalse(nouveau.exists()); assertFalse(ancien.exists())
    }

    @Test
    fun `critere 1 - R2 dossier a moitie efface (sans temoin), nouveau pret - la bascule est menee a terme`() {
        poserPartiel(cible); poserNouveauPret(); poserAncien(ancien)
        val i = installation()
        assertTrue(i.estInstalle)
        erreur<ArchiveIllisible>(i.installer(archive("tronquee.zip")))
        assertTrue(File(cible, "nouveau.txt").isFile)
        assertFalse(File(cible, "partiel.txt").exists(), "les restes du dossier sans témoin doivent partir")
        assertFalse(File(cible, "ancien.txt").exists())
        assertTrue(File(cible, TEMOIN).isFile)
        assertFalse(nouveau.exists()); assertFalse(ancien.exists())
    }

    @Test
    fun `critere 1 - R2 sans ancien - le nouveau pret devient le modele`() {
        poserNouveauPret()
        val i = installation()
        assertTrue(i.estInstalle)
        erreur<ArchiveIllisible>(i.installer(archive("tronquee.zip")))
        assertTrue(File(cible, "nouveau.txt").isFile)
        assertTrue(File(cible, TEMOIN).isFile)
        assertFalse(nouveau.exists())
    }

    @Test
    fun `critere 1 - R3 dossier absent, nouveau absent, ancien a temoin - l'ancien revient`() {
        poserAncien(ancien)
        val i = installation()
        assertTrue(i.estInstalle)
        erreur<ArchiveIllisible>(i.installer(archive("tronquee.zip")))
        assertAncienIntact("R3 dossier absent")
    }

    @Test
    fun `critere 1 - R3 dossier et nouveau a moitie ecrits (sans temoin), ancien a temoin - l'ancien revient`() {
        poserPartiel(cible); poserPartiel(nouveau); poserAncien(ancien)
        val i = installation()
        assertTrue(i.estInstalle)
        erreur<ArchiveIllisible>(i.installer(archive("tronquee.zip")))
        assertAncienIntact("R3 restes")
        assertFalse(File(cible, "partiel.txt").exists())
    }

    @Test
    fun `critere 1 - R4 aucun temoin nulle part - tout est efface et le modele n'est plus installe`() {
        poserPartiel(cible); poserPartiel(nouveau); poserPartiel(ancien)
        val i = installation()
        assertFalse(i.estInstalle)
        erreur<ArchiveIllisible>(i.installer(archive("tronquee.zip")))
        assertFalse(cible.exists(), "D doit être effacé")
        assertFalse(nouveau.exists(), "N doit être effacé")
        assertFalse(ancien.exists(), "A doit être effacé")
        assertFalse(i.estInstalle)
    }

    @Test
    fun `critere 1 - R4 un seul reste sans temoin - efface lui aussi`() {
        poserPartiel(nouveau)
        val i = installation()
        assertFalse(i.estInstalle)
        erreur<ArchiveIllisible>(i.installer(archive("tronquee.zip")))
        assertFalse(nouveau.exists())
        assertFalse(cible.exists())
    }

    @Test
    fun `critere 1 - la reprise s'applique aussi au debut de telecharger`() {
        poserAncien(ancien)
        val i = installation(url = File(entrees, "inexistant.zip").toURI().toString())
        assertTrue(i.telecharger().isFailure)
        assertAncienIntact("reprise par telecharger")
    }

    @Test
    fun `critere 1 - supprimer efface dossier, nouveau, ancien et archive telechargee`() {
        poserAncien(); poserNouveauPret(); poserAncien(ancien); File(racine, "modele.zip").writeText("zip")
        val i = installation()
        i.supprimer()
        assertFalse(cible.exists()); assertFalse(nouveau.exists()); assertFalse(ancien.exists())
        assertFalse(zipTemporaire.exists())
        assertFalse(i.estInstalle)
    }

    // ================================================================ critère 2 : archives

    @Test
    fun `critere 2 - bonne archive avec racine - installee, dossier de tete retire, temoin ecrit, l'ancien remplace`() {
        poserAncien()
        val i = installation()
        assertTrue(i.installer(archive("bonne.zip")).isSuccess)
        assertTrue(i.estInstalle)
        assertNouveauInstalle("bonne")
        assertEquals("gr\n", File(cible, "graph/Gr.fst").readText())
    }

    @Test
    fun `critere 2 - bonne archive sans dossier de tete - installee`() {
        poserAncien()
        val i = installation()
        assertTrue(i.installer(archive("bonne-sans-racine.zip")).isSuccess)
        assertTrue(i.estInstalle)
        assertNouveauInstalle("sans racine")
        assertEquals("gr\n", File(cible, "graph/Gr.fst").readText())
        assertEquals("hclr\n", File(cible, "graph/HCLr.fst").readText())
    }

    @Test
    fun `critere 2 - premiere installation, sans ancien - installee`() {
        val i = installation()
        assertFalse(i.estInstalle)
        assertTrue(i.installer(archive("bonne.zip")).isSuccess)
        assertTrue(i.estInstalle)
        assertNouveauInstalle("premiere installation")
    }

    @Test
    fun `critere 2 - archive tronquee - ArchiveIllisible, l'ancien est intact`() {
        poserAncien()
        val e = erreur<ArchiveIllisible>(installation().installer(archive("tronquee.zip")))
        assertTrue(e.raison.isNotBlank())
        assertAncienIntact("tronquée")
    }

    @Test
    fun `critere 2 - archive de 0 octet - ArchiveIllisible, l'ancien est intact`() {
        poserAncien()
        erreur<ArchiveIllisible>(installation().installer(archive("vide.zip")))
        assertAncienIntact("vide")
    }

    @Test
    fun `critere 2 - zip sans aucune entree - StructureInvalide, l'ancien est intact`() {
        poserAncien()
        erreur<StructureInvalide>(installation().installer(archive("sans-entree.zip")))
        assertAncienIntact("sans entrée")
    }

    @Test
    fun `critere 2 - racine seule, rien dedans - StructureInvalide, l'ancien est intact`() {
        poserAncien()
        erreur<StructureInvalide>(installation().installer(archive("racine-seule.zip")))
        assertAncienIntact("racine seule")
    }

    @Test
    fun `critere 2 - sans am-final-mdl - StructureInvalide, l'ancien est intact`() {
        poserAncien()
        erreur<StructureInvalide>(installation().installer(archive("sans-final-mdl.zip")))
        assertAncienIntact("sans final.mdl")
    }

    @Test
    fun `critere 2 - modele a HCLG-fst sans HCLr-fst - SansGrammaireDynamique, l'ancien est intact`() {
        poserAncien()
        erreur<SansGrammaireDynamique>(installation().installer(archive("hclg.zip")))
        assertAncienIntact("HCLG")
    }

    @Test
    fun `critere 2 - archive avec __MACOSX et DS_Store - installee, sans ces deux restes`() {
        poserAncien()
        val i = installation()
        assertTrue(i.installer(archive("avec-macosx.zip")).isSuccess)
        assertNouveauInstalle("macosx")
        assertFalse(File(cible, "__MACOSX").exists())
        assertFalse(File(cible, "vosk-model-test/__MACOSX").exists())
        assertFalse(File(cible, "._final.mdl").exists())
        assertFalse(File(cible, ".DS_Store").exists())
        assertFalse(File(racine, "__MACOSX").exists())
    }

    @Test
    fun `critere 2 - sans graph-disambig_tid-int - StructureInvalide, l'ancien est intact`() {
        poserAncien()
        erreur<StructureInvalide>(installation().installer(archive("sans-disambig.zip")))
        assertAncienIntact("sans disambig_tid.int")
    }

    @Test
    fun `critere 2 - bonne archive avec un fichier vide (ivector-online_cmvn-conf) - installee, le fichier vide est la`() {
        poserAncien()
        val i = installation()
        assertTrue(i.installer(archive("avec-fichier-vide.zip")).isSuccess)
        assertTrue(i.estInstalle)
        assertNouveauInstalle("fichier vide")
        val vide = File(cible, "ivector/online_cmvn.conf")
        assertTrue(vide.isFile, "le fichier vide doit être installé")
        assertEquals(0L, vide.length())
    }

    @Test
    fun `critere 2 - entree qui sort du dossier - ArchiveIllisible, rien n'ecrit hors du dossier, l'ancien est intact`() {
        // L'entrée « vosk-model-test/../../evade-017.txt » viserait le parent de « modele.nouveau »
        // (ou de la racine de l'archive) : le dossier temporaire du test, ou /tmp. On nettoie avant
        // et après les emplacements possibles, pour qu'un échec n'en laisse pas dans /tmp.
        val traces = listOf(
            File(racine, "evade-017.txt"),
            File(racine.parentFile, "evade-017.txt"),
            File(cible, "evade-017.txt"),
            File(nouveau, "evade-017.txt"),
        )
        traces.forEach { it.delete() }
        try {
            poserAncien()
            erreur<ArchiveIllisible>(installation().installer(archive("evasion.zip")))
            traces.forEach { assertFalse(it.exists(), "${it.path} ne doit pas exister") }
            assertAncienIntact("évasion")
        } finally {
            traces.forEach { it.delete() }
        }
    }

    @Test
    fun `critere 2 - entree a-point-point-b qui reste sous la racine - ArchiveIllisible, l'ancien est intact`() {
        poserAncien()
        erreur<ArchiveIllisible>(installation().installer(archive("point-point-interne.zip")))
        assertFalse(File(cible, "b").exists())
        assertFalse(File(racine, "b").exists())
        assertAncienIntact("a/../b")
    }

    // ================================================================ critère 3 : empreinte

    @Test
    fun `critere 3 - empreinte fausse - EmpreinteInattendue avec l'attendue et l'obtenue, l'ancien est intact`() {
        poserAncien()
        val fausse = "00".repeat(32)
        val e = erreur<EmpreinteInattendue>(installation(Empreinte.Sha256(fausse)).installer(archive("bonne.zip")))
        assertEquals(fausse, e.attendue)
        assertEquals(reference.getValue("bonne.zip"), e.obtenue)
        assertAncienIntact("empreinte fausse")
    }

    @Test
    fun `critere 3 - empreinte juste - installee`() {
        poserAncien()
        assertTrue(installation(sha("bonne.zip")).installer(archive("bonne.zip")).isSuccess)
        assertNouveauInstalle("empreinte juste")
    }

    @Test
    fun `critere 3 - empreinte en majuscules - acceptee`() {
        poserAncien()
        val maj = Empreinte.Sha256(reference.getValue("bonne.zip").uppercase())
        assertTrue(installation(maj).installer(archive("bonne.zip")).isSuccess)
        assertNouveauInstalle("majuscules")
    }

    @Test
    fun `critere 3 - empreinte non controlee - l'appelant l'a ecrit, l'archive est installee`() {
        poserAncien()
        assertTrue(installation(Empreinte.NonControlee).installer(archive("bonne.zip")).isSuccess)
        assertNouveauInstalle("non contrôlée")
    }

    @Test
    fun `critere 3 - empreinte vide - EmpreinteMalFormee a la construction`() {
        val e = assertFailsWith<EmpreinteMalFormee> { Empreinte.Sha256("") }
        assertEquals("", e.valeur)
    }

    @Test
    fun `critere 3 - empreinte de 63 caracteres - EmpreinteMalFormee a la construction`() {
        val v = "a".repeat(63)
        val e = assertFailsWith<EmpreinteMalFormee> { Empreinte.Sha256(v) }
        assertEquals(v, e.valeur)
    }

    @Test
    fun `critere 3 - empreinte de 65 caracteres - EmpreinteMalFormee a la construction`() {
        val v = "a".repeat(65)
        assertEquals(v, assertFailsWith<EmpreinteMalFormee> { Empreinte.Sha256(v) }.valeur)
    }

    @Test
    fun `critere 3 - empreinte de 64 caracteres dont un non hexadecimal - EmpreinteMalFormee a la construction`() {
        val v = "a".repeat(63) + "g"
        assertEquals(v, assertFailsWith<EmpreinteMalFormee> { Empreinte.Sha256(v) }.valeur)
    }

    @Test
    fun `critere 3 - empreinte de 64 caracteres suivie d'un retour a la ligne - EmpreinteMalFormee`() {
        val v = "a".repeat(64) + "\n"
        assertFailsWith<EmpreinteMalFormee> { Empreinte.Sha256(v) }
    }

    @Test
    fun `critere 3 - l'empreinte est controlee avant la structure - HCLG avec fausse empreinte donne EmpreinteInattendue`() {
        poserAncien()
        erreur<EmpreinteInattendue>(installation(Empreinte.Sha256("00".repeat(32))).installer(archive("hclg.zip")))
        assertAncienIntact("ordre empreinte/structure")
    }

    @Test
    fun `critere 3 - l'archive est ouverte avant l'empreinte - tronquee avec fausse empreinte donne ArchiveIllisible`() {
        poserAncien()
        erreur<ArchiveIllisible>(installation(Empreinte.Sha256("00".repeat(32))).installer(archive("tronquee.zip")))
        assertAncienIntact("ordre ouverture/empreinte")
    }

    @Test
    fun `critere 3 - empreinte reste publique et juste - InstallationModele-empreinte(InputStream) rend celle de sha256sum`() {
        val calculee = archive("bonne.zip").inputStream().use { InstallationModele.empreinte(it) }
        assertEquals(reference.getValue("bonne.zip"), calculee.lowercase())
    }

    // ================================================================ critère 4 : telecharger

    @Test
    fun `critere 4 - telechargement par HTTP puis installation - nominal, progression terminee, archive temporaire effacee`() {
        poserAncien()
        val i = installation(url = serveurComplet("bonne.zip"))
        val etapes = mutableListOf<Float>()
        assertTrue(i.telecharger(progression = { etapes += it }).isSuccess)
        assertEquals(1f, etapes.last())
        assertNouveauInstalle("téléchargement")
        assertFalse(zipTemporaire.exists())
    }

    @Test
    fun `critere 4 - telechargement par URL fichier - nominal`() {
        val i = installation(url = archive("bonne.zip").toURI().toString())
        val etapes = mutableListOf<Float>()
        assertTrue(i.telecharger(progression = { etapes += it }).isSuccess)
        assertTrue(i.estInstalle)
        assertEquals(1f, etapes.last())
        assertNouveauInstalle("URL fichier")
    }

    @Test
    fun `critere 4 - annulation par l'utilisateur - Interrompu, l'ancien est intact, archive temporaire effacee`() {
        poserAncien()
        val i = installation(url = serveurComplet("bonne.zip"))
        erreur<Interrompu>(i.telecharger(continuer = { false }))
        assertAncienIntact("annulation")
        assertTrue(i.estInstalle)
        assertFalse(zipTemporaire.exists())
    }

    @Test
    fun `critere 4 - test inverse - installe puis annulation - le modele installe survit`() {
        val a = archive("bonne.zip")
        val i = installation(url = a.toURI().toString())
        assertTrue(i.telecharger().isSuccess)
        assertTrue(i.estInstalle)
        erreur<Interrompu>(i.telecharger(continuer = { false }))
        assertTrue(i.estInstalle, "l'annulation ne doit plus effacer le modèle installé")
        assertNouveauInstalle("après annulation")
    }

    @Test
    fun `critere 4 - coupure du reseau, flux tronque - ArchiveIllisible, l'ancien est intact, archive temporaire effacee`() {
        poserAncien()
        val octets = archive("bonne.zip").readBytes()
        val i = installation(url = serveur(octets.size.toLong(), octets.copyOf(octets.size / 2)))
        erreur<ArchiveIllisible>(i.telecharger())
        assertAncienIntact("coupure")
        assertFalse(zipTemporaire.exists())
    }

    @Test
    fun `critere 4 - serveur injoignable - echec, l'ancien est intact`() {
        poserAncien()
        val s = ServerSocket(0).also { it.close() } // port libéré : plus personne n'écoute
        val i = installation(url = "http://127.0.0.1:${s.localPort}/modele.zip")
        assertTrue(i.telecharger().isFailure)
        assertAncienIntact("injoignable")
        assertFalse(zipTemporaire.exists())
    }

    @Test
    fun `critere 4 - Content-Length trop grand pour la place disponible - EspaceInsuffisant, l'ancien est intact`() {
        poserAncien()
        val i = installation(url = serveur(1L shl 50, ByteArray(0)), espace = { PLENTY })
        val e = erreur<EspaceInsuffisant>(i.telecharger())
        assertTrue(e.manquant > 0)
        assertAncienIntact("content-length")
        assertFalse(zipTemporaire.exists())
    }

    @Test
    fun `critere 4 - empreinte fausse au telechargement - EmpreinteInattendue, l'ancien est intact`() {
        poserAncien()
        val i = installation(Empreinte.Sha256("00".repeat(32)), serveurComplet("bonne.zip"))
        erreur<EmpreinteInattendue>(i.telecharger())
        assertAncienIntact("empreinte au téléchargement")
        assertFalse(zipTemporaire.exists())
    }

    @Test
    fun `critere 4 - archive telechargee sans HCLr - SansGrammaireDynamique, l'ancien est intact`() {
        poserAncien()
        val i = installation(url = serveurComplet("hclg.zip"))
        erreur<SansGrammaireDynamique>(i.telecharger())
        assertAncienIntact("HCLG téléchargé")
        assertFalse(zipTemporaire.exists())
    }

    // ================================================================ critère 5 : espace

    @Test
    fun `critere 5 - espace injecte a zero - EspaceInsuffisant(manquant), rien n'est efface`() {
        poserAncien()
        val e = erreur<EspaceInsuffisant>(installation(espace = { 0L }).installer(archive("bonne.zip")))
        assertTrue(e.manquant >= TAILLE_BONNE, "manquant (${e.manquant}) doit couvrir au moins les $TAILLE_BONNE octets décompressés")
        assertAncienIntact("espace")
    }

    @Test
    fun `critere 5 - espace largement suffisant - installee`() {
        poserAncien()
        assertTrue(installation(espace = { PLENTY }).installer(archive("bonne.zip")).isSuccess)
        assertNouveauInstalle("espace suffisant")
    }

    @Test
    fun `critere 5 - l'espace est demande pour le dossier parent du modele`() {
        val vus = mutableListOf<File>()
        installation(espace = { vus += it; PLENTY }).installer(archive("bonne.zip"))
        assertTrue(vus.isNotEmpty(), "espaceDisponible n'a pas été consulté")
        vus.forEach { assertEquals(racine.canonicalFile, it.canonicalFile) }
    }

    @Test
    fun `critere 5 - espace insuffisant sans ancien - rien n'est cree`() {
        erreur<EspaceInsuffisant>(installation(espace = { 0L }).installer(archive("bonne.zip")))
        assertFalse(cible.exists())
        assertFalse(nouveau.exists())
    }

    // ================================================================ critère 6 : annulation pendant la décompression

    @Test
    fun `critere 6 - annulation pendant la decompression - Interrompu, l'ancien est intact, nouveau absent`() {
        poserAncien()
        // Faux tant que la décompression n'a pas créé « modele.nouveau » : prouve que continuer
        // est consulté pendant la décompression, pas seulement avant.
        val i = installation()
        erreur<Interrompu>(i.installer(archive("bonne.zip"), continuer = { !nouveau.exists() }))
        assertAncienIntact("annulation pendant décompression")
        assertTrue(i.estInstalle)
    }

    @Test
    fun `critere 6 - annulation pendant la decompression, sans ancien - rien n'est installe, nouveau absent`() {
        val i = installation()
        erreur<Interrompu>(i.installer(archive("bonne.zip"), continuer = { !nouveau.exists() }))
        assertFalse(i.estInstalle)
        assertFalse(cible.exists())
        assertFalse(nouveau.exists())
    }

    // ================================================================ critère 8 : estInstalle n'écrit rien

    private fun assertEstInstalleSansEcriture(attendu: Boolean) {
        val avant = photo(racine)
        val i = installation()
        assertEquals(attendu, i.estInstalle)
        assertEquals(attendu, i.estInstalle)
        assertEquals(avant, photo(racine), "estInstalle a modifié le disque")
    }

    @Test
    fun `critere 8 - estInstalle n'ecrit rien en R1`() {
        poserAncien(); poserPartiel(nouveau); poserPartiel(ancien)
        assertEstInstalleSansEcriture(true)
    }

    @Test
    fun `critere 8 - estInstalle n'ecrit rien en R2`() {
        poserPartiel(cible); poserNouveauPret(); poserAncien(ancien)
        assertEstInstalleSansEcriture(true)
    }

    @Test
    fun `critere 8 - estInstalle n'ecrit rien en R3`() {
        poserPartiel(cible); poserPartiel(nouveau); poserAncien(ancien)
        assertEstInstalleSansEcriture(true)
    }

    @Test
    fun `critere 8 - estInstalle n'ecrit rien en R4`() {
        poserPartiel(cible); poserPartiel(nouveau); poserPartiel(ancien)
        assertEstInstalleSansEcriture(false)
    }

    @Test
    fun `critere 8 - le constructeur n'ecrit rien - ni dossier, ni verrou, ni reprise`() {
        poserPartiel(cible); poserPartiel(nouveau); poserAncien(ancien)
        val avant = photo(racine)
        installation()
        assertEquals(avant, photo(racine), "le constructeur a modifié le disque")
    }

    @Test
    fun `critere 8 - estInstalle n'ecrit rien quand le parent est vide - ni dossier, ni verrou`() {
        assertEstInstalleSansEcriture(false)
        assertEquals(emptyList<String>(), racine.list()!!.toList())
    }

    private companion object {
        const val TEMOIN = ".installe"
        /** 6 + 5 + 5 + 3 + 9 + 8 octets décompressés dans bonne.zip (voir fabriquer.sh). */
        const val TAILLE_BONNE = 36L
        /** 1 Tio : très au-dessus du besoin, sans risque de dépassement avec une marge. */
        const val PLENTY = 1L shl 40
    }
}
