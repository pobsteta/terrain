package fr.nemeton.terrain.style

import java.io.File
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/**
 * Lecture du référentiel `docs/referentiels/style-marculus.md` et de son CSV, pour les tests
 * d'égalité au style de Marculus (mission 021, ADR 0006).
 *
 * Aucune valeur attendue n'est calculée par le code testé : tout vient de ces deux fichiers.
 */
internal object ReferentielMarculus {

    /** Une valeur lue et sa position (numéro de ligne, à partir de 1) dans le fichier source. */
    class Ligne<T>(val numero: Int, val valeur: T)

    private fun fichier(vararg candidats: String): File =
        candidats.map(::File).firstOrNull { it.isFile }
            ?: error("introuvable depuis ${File(".").absolutePath} : ${candidats.toList()}")

    private val markdown: List<String> by lazy {
        fichier("../docs/referentiels/style-marculus.md", "docs/referentiels/style-marculus.md").readLines()
    }

    /** Lignes (numéro, texte) d'une section `## <prefixe>` jusqu'à la suivante. */
    fun section(prefixe: String): List<Pair<Int, String>> {
        val debut = markdown.indexOfFirst { it.startsWith("## $prefixe") }
        assertTrue(debut >= 0, "section « $prefixe » absente de style-marculus.md")
        val fin = (debut + 1 until markdown.size).firstOrNull { markdown[it].startsWith("## ") } ?: markdown.size
        return (debut until fin).map { (it + 1) to markdown[it] }
    }

    /** Cellules d'une ligne de tableau Markdown, sans les barres ni les espaces. */
    fun cellules(ligne: String): List<String> =
        ligne.trim().removePrefix("|").removeSuffix("|").split('|').map { it.trim() }

    /** Le CSV `theme,role,argb` : (thème, rôle) -> argb, avec le numéro de ligne du CSV. */
    fun couleurs(): Map<Pair<String, String>, Ligne<Long>> {
        val flux = ReferentielMarculus::class.java.getResourceAsStream("/jetons/style-marculus.csv")
            ?: error("jetons/style-marculus.csv introuvable dans les ressources de test")
        val lignes = flux.bufferedReader().use { it.readLines() }
        assertEquals("theme,role,argb", lignes.first(), "en-tête du CSV")
        val m = LinkedHashMap<Pair<String, String>, Ligne<Long>>()
        lignes.forEachIndexed { i, l ->
            if (i == 0 || l.isBlank()) return@forEachIndexed
            val c = l.split(',')
            assertEquals(3, c.size, "style-marculus.csv L${i + 1} : colonnes de « $l »")
            assertTrue(c[2].startsWith("0x"), "style-marculus.csv L${i + 1} : ARGB en 0x… : ${c[2]}")
            val cle = c[0] to c[1]
            assertTrue(cle !in m, "style-marculus.csv L${i + 1} : doublon $cle")
            m[cle] = Ligne(i + 1, java.lang.Long.parseLong(c[2].drop(2), 16))
        }
        return m
    }

    /** Formes (§ 2) : nom -> rayon en dp, d'après les lignes de la forme « | `extraSmall` | 4 dp | … ». */
    fun formes(): Map<String, Ligne<Int>> {
        val m = LinkedHashMap<String, Ligne<Int>>()
        for ((n, l) in section("2.")) {
            if (!l.startsWith("|")) continue
            val c = cellules(l)
            if (c.size < 2 || !Regex("`\\w+`").matches(c[0]) || !Regex("\\d+ dp").matches(c[1])) continue
            m[c[0].trim('`')] = Ligne(n, c[1].removeSuffix(" dp").toInt())
        }
        return m
    }

    class Typo(val taille: Int, val interligne: Int, val graisse: Int, val interlettrage: Double)

    /** Typographie (§ 3) : nom -> (taille, interligne, graisse, interlettrage), lignes « | `displayLarge` | 57 sp | … ». */
    fun typographie(): Map<String, Ligne<Typo>> {
        val m = LinkedHashMap<String, Ligne<Typo>>()
        for ((n, l) in section("3.")) {
            if (!l.startsWith("|")) continue
            val c = cellules(l)
            if (c.size < 6 || !Regex("`\\w+`").matches(c[0]) || !Regex("\\d+ sp").matches(c[1])) continue
            val graisse = Regex("^(\\d+) ").find(c[3])?.groupValues?.get(1)?.toInt()
                ?: error("référentiel L$n : graisse illisible « ${c[3]} »")
            m[c[0].trim('`')] = Ligne(
                n,
                Typo(
                    c[1].removeSuffix(" sp").toInt(),
                    c[2].removeSuffix(" sp").toInt(),
                    graisse,
                    c[4].removeSuffix(" sp").toDouble(),
                ),
            )
        }
        return m
    }
}
