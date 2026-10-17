package fr.nemeton.terrain.voix

import java.io.File
import java.nio.file.Files
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class InstallationModeleTest {
    private val racine: File = Files.createTempDirectory("vosk").toFile()
    private val cible = File(racine, "modele")
    private val cache = File(racine, "cache").apply { mkdirs() }

    private fun archive(vararg entrees: Pair<String, String>): File = File(racine, "m${entrees.size}-${System.nanoTime()}.zip").also { f ->
        ZipOutputStream(f.outputStream()).use { z ->
            entrees.forEach { (nom, contenu) ->
                z.putNextEntry(ZipEntry(nom))
                z.write(contenu.toByteArray())
                z.closeEntry()
            }
        }
    }

    private val modele get() = archive("vosk-model-small-fr-0.22/" to "", "vosk-model-small-fr-0.22/graph/Gr.fst" to "fst", "vosk-model-small-fr-0.22/conf/model.conf" to "conf")

    @Test
    fun `installation depuis une archive - dossier racine retire, temoin ecrit`() {
        val i = InstallationModele(cible, cache, "https://exemple/modele.zip")
        assertFalse(i.estInstalle)
        assertTrue(i.installer(modele).isSuccess)
        assertTrue(i.estInstalle)
        assertEquals("fst", File(cible, "graph/Gr.fst").readText())
        i.supprimer()
        assertFalse(i.estInstalle)
    }

    @Test
    fun `empreinte SHA-256 controlee`() {
        val a = modele
        val bonne = a.inputStream().use { InstallationModele.empreinte(it) }
        assertTrue(InstallationModele(cible, cache, "u", sha256 = bonne.uppercase()).installer(a).isSuccess)
        val mauvaise = InstallationModele(cible, cache, "u", sha256 = "00".repeat(32))
        assertTrue(mauvaise.installer(a).isFailure)
        assertFalse(mauvaise.estInstalle)
    }

    @Test
    fun `entree hors du dossier refusee`() {
        val i = InstallationModele(cible, cache, "u")
        assertTrue(i.installer(archive("racine/../../evade.txt" to "x")).isFailure)
        assertFalse(File(racine, "evade.txt").exists())
        assertFalse(i.estInstalle)
    }

    @Test
    fun `telechargement - par URL, puis interruption`() {
        val a = modele
        val i = InstallationModele(cible, cache, a.toURI().toString())
        val etapes = mutableListOf<Float>()
        assertTrue(i.telecharger(progression = { etapes += it }).isSuccess)
        assertTrue(i.estInstalle)
        assertEquals(1f, etapes.last())
        assertTrue(i.telecharger(continuer = { false }).isFailure)
        assertFalse(i.estInstalle)
    }
}
