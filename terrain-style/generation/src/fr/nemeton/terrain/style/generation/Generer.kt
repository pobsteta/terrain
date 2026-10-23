package fr.nemeton.terrain.style.generation

import dynamiccolor.ColorSpec.SpecVersion
import dynamiccolor.DynamicScheme
import dynamiccolor.DynamicScheme.Platform
import dynamiccolor.Variant
import fr.nemeton.terrain.style.Contraste
import hct.Hct
import palettes.TonalPalette
import scheme.SchemeContent
import utils.MathUtils
import java.io.File
import java.util.Locale
import kotlin.math.floor

/**
 * Génération des jetons de `terrain-style` (ADR 0001, docs/spec.md).
 *
 * Construit les schémas clair et sombre par les sources Kotlin de Material Color Utilities
 * recopiées dans `generation/mcu/` (commit [Reglages.COMMIT_MCU]), puis écrit, sans date ni
 * valeur dépendante de la machine :
 *  - `src/main/kotlin/fr/nemeton/terrain/style/StyleTerrain.kt` (les jetons figés) ;
 *  - `src/test/resources/jetons/jetons.csv` (`theme,role,argb`) ;
 *  - `generation/nuancier.svg` (les deux schémas à côté de la couleur de départ).
 *
 * Les couleurs d'état (ADR 0002) : `reussi` est généré par MCU (palette de [Reglages.REUSSI] à la
 * place de la palette d'erreur), `alerte` est figée ([Reglages.ALERTE], [Reglages.SUR_ALERTE]).
 *
 * Aucune valeur n'est retouchée : un rôle qui ne tient pas 7:1 en texte sur l'un de ses fonds
 * est mis dans `interditsEnTexte`, avec `alerte.couleur` par décision.
 */
object Reglages {
    const val COMMIT_MCU = "bd811e4ca2ab94c4ce3cadc89c19b1820b9704b3"
    const val NOM_SOURCE = "VertMarculus"
    const val SOURCE = 0xFF374742L
    /** ADR 0004 (amende l'ADR 0001) : CONTENT, pour qu'en sombre tertiary ne soit pas identique à error. */
    val VARIANTE = Variant.CONTENT
    /** Imposée par CONTENT sur PHONE (`DynamicScheme.maybeFallbackSpecVersion`), vérifiée à la construction. */
    val SPEC = SpecVersion.SPEC_2021
    const val CONTRASTE = 1.0
    val PLATEFORME = Platform.PHONE
    const val SEUIL_TEXTE = 7.0

    /**
     * Vert de départ de `reussi` : RAL 6032 d'après Wikipédia « List of RAL colours »
     * (docs/referentiels/couleurs-etat.md, R2), seul candidat dans la région verte d'ISO 3864-4 ;
     * choix de Pascal du 2026-10-23 (ADR 0002). **Non harmonisé** vers [SOURCE] : sa palette est
     * celle de la couleur telle quelle.
     */
    const val REUSSI = 0xFF0F8558L
    const val SOURCE_REUSSI = "RAL 6032 (Wikipédia « List of RAL colours »), non harmonisé"

    /** Jaune de sécurité, en `couleur` et `conteneur` d'`alerte`, figé hors MCU (ADR 0002). */
    const val ALERTE = 0xFFE2AA00L
    /** Noir de contraste du jaune, en `surCouleur` et `surConteneur` d'`alerte` (ADR 0002). */
    const val SUR_ALERTE = 0xFF000000L
    const val SOURCE_ALERTE = "ISO 3864-4:2011, Table 1 (β 0,45, D65, converti en sRGB), noir de contraste"
}

/** Les 48 rôles de `ColorScheme` (Compose Material 3 1.4.0), dans l'ordre du constructeur. */
val ROLES: List<Pair<String, (DynamicScheme) -> Int>> = listOf(
    "primary" to { s -> s.primary }, "onPrimary" to { s -> s.onPrimary },
    "primaryContainer" to { s -> s.primaryContainer }, "onPrimaryContainer" to { s -> s.onPrimaryContainer },
    "inversePrimary" to { s -> s.inversePrimary },
    "secondary" to { s -> s.secondary }, "onSecondary" to { s -> s.onSecondary },
    "secondaryContainer" to { s -> s.secondaryContainer }, "onSecondaryContainer" to { s -> s.onSecondaryContainer },
    "tertiary" to { s -> s.tertiary }, "onTertiary" to { s -> s.onTertiary },
    "tertiaryContainer" to { s -> s.tertiaryContainer }, "onTertiaryContainer" to { s -> s.onTertiaryContainer },
    "background" to { s -> s.background }, "onBackground" to { s -> s.onBackground },
    "surface" to { s -> s.surface }, "onSurface" to { s -> s.onSurface },
    "surfaceVariant" to { s -> s.surfaceVariant }, "onSurfaceVariant" to { s -> s.onSurfaceVariant },
    "surfaceTint" to { s -> s.surfaceTint },
    "inverseSurface" to { s -> s.inverseSurface }, "inverseOnSurface" to { s -> s.inverseOnSurface },
    "error" to { s -> s.error }, "onError" to { s -> s.onError },
    "errorContainer" to { s -> s.errorContainer }, "onErrorContainer" to { s -> s.onErrorContainer },
    "outline" to { s -> s.outline }, "outlineVariant" to { s -> s.outlineVariant }, "scrim" to { s -> s.scrim },
    "surfaceBright" to { s -> s.surfaceBright }, "surfaceDim" to { s -> s.surfaceDim },
    "surfaceContainer" to { s -> s.surfaceContainer }, "surfaceContainerHigh" to { s -> s.surfaceContainerHigh },
    "surfaceContainerHighest" to { s -> s.surfaceContainerHighest },
    "surfaceContainerLow" to { s -> s.surfaceContainerLow }, "surfaceContainerLowest" to { s -> s.surfaceContainerLowest },
    "primaryFixed" to { s -> s.primaryFixed }, "primaryFixedDim" to { s -> s.primaryFixedDim },
    "onPrimaryFixed" to { s -> s.onPrimaryFixed }, "onPrimaryFixedVariant" to { s -> s.onPrimaryFixedVariant },
    "secondaryFixed" to { s -> s.secondaryFixed }, "secondaryFixedDim" to { s -> s.secondaryFixedDim },
    "onSecondaryFixed" to { s -> s.onSecondaryFixed }, "onSecondaryFixedVariant" to { s -> s.onSecondaryFixedVariant },
    "tertiaryFixed" to { s -> s.tertiaryFixed }, "tertiaryFixedDim" to { s -> s.tertiaryFixedDim },
    "onTertiaryFixed" to { s -> s.onTertiaryFixed }, "onTertiaryFixedVariant" to { s -> s.onTertiaryFixedVariant },
)

/** Le schéma MCU d'un thème ; refuse tout schéma qui ne serait pas en [Reglages.SPEC]. */
fun schemaMcu(sombre: Boolean): DynamicScheme {
    check(Reglages.VARIANTE == Variant.CONTENT) { "le script construit SchemeContent" }
    val s = SchemeContent(
        Hct.fromInt(Reglages.SOURCE.toInt()),
        sombre,
        Reglages.CONTRASTE,
        Reglages.SPEC,
        Reglages.PLATEFORME,
    )
    check(s.specVersion == Reglages.SPEC) { "specVersion ${s.specVersion} au lieu de ${Reglages.SPEC}" }
    check(s.variant == Reglages.VARIANTE && s.isDark == sombre && s.contrastLevel == Reglages.CONTRASTE)
    check(s.platform == Reglages.PLATEFORME)
    return s
}

/** ARGB `Int` de MCU en `Long` 0xFFRRGGBB, sans extension de signe. */
fun versLong(argb: Int): Long = argb.toLong() and 0xFFFFFFFFL

/** Rôle -> ARGB pour un thème, dans l'ordre de [ROLES]. */
fun jetons(sombre: Boolean): Map<String, Long> {
    val s = schemaMcu(sombre)
    return ROLES.associateTo(LinkedHashMap()) { (nom, f) -> nom to versLong(f(s)) }
}

/** Les deux couleurs d'état, dans l'ordre des champs de `Schema`. */
val SEMANTIQUES = listOf("reussi", "alerte")

/** Les huit jetons d'état (`reussi.couleur`…), dans l'ordre des champs de `Semantique`. */
val NOMS_SEMANTIQUES = SEMANTIQUES.flatMap { x -> listOf("couleur", "surCouleur", "conteneur", "surConteneur").map { "$x.$it" } }

/** Interdits en texte par décision, sans qu'un couple échoue (ADR 0002). */
val INTERDITS_PAR_DECISION = setOf("alerte.couleur")

/**
 * Le schéma de `reussi` (ADR 0002) : celui du thème, palette d'erreur remplacée par celle de
 * [Reglages.REUSSI]. Ses rôles `error*` ont donc les tons et les courbes de contraste d'`error`.
 */
fun schemaReussi(sombre: Boolean): DynamicScheme {
    val b = schemaMcu(sombre)
    val s = DynamicScheme(
        b.sourceColorHctList, b.variant, b.isDark, b.contrastLevel, b.platform, b.specVersion,
        b.primaryPalette, b.secondaryPalette, b.tertiaryPalette, b.neutralPalette, b.neutralVariantPalette,
        TonalPalette.fromInt(Reglages.REUSSI.toInt()),
    )
    check(s.specVersion == Reglages.SPEC) { "specVersion ${s.specVersion} au lieu de ${Reglages.SPEC}" }
    return s
}

/** Les huit jetons d'état d'un thème : `reussi` lu dans [schemaReussi], `alerte` figée. */
fun semantiques(sombre: Boolean): Map<String, Long> {
    val r = schemaReussi(sombre)
    return linkedMapOf(
        "reussi.couleur" to versLong(r.error), "reussi.surCouleur" to versLong(r.onError),
        "reussi.conteneur" to versLong(r.errorContainer), "reussi.surConteneur" to versLong(r.onErrorContainer),
        "alerte.couleur" to Reglages.ALERTE, "alerte.surCouleur" to Reglages.SUR_ALERTE,
        "alerte.conteneur" to Reglages.ALERTE, "alerte.surConteneur" to Reglages.SUR_ALERTE,
    )
}

/** Les 48 rôles puis les huit jetons d'état. */
fun tousLesJetons(sombre: Boolean): Map<String, Long> = LinkedHashMap(jetons(sombre)).apply { putAll(semantiques(sombre)) }

/** Écart de teinte HCT, en degrés (0 à 180), de `reussi.couleur` à `primary` (fiche 007). */
fun ecartReussiPrimary(sombre: Boolean): Double {
    val j = tousLesJetons(sombre)
    return MathUtils.differenceDegrees(
        Hct.fromInt(j.getValue("reussi.couleur").toInt()).hue,
        Hct.fromInt(j.getValue("primary").toInt()).hue,
    )
}

private val SURFACES = listOf(
    "surface", "surfaceDim", "surfaceBright", "surfaceContainer", "surfaceContainerHigh",
    "surfaceContainerHighest", "surfaceContainerLow", "surfaceContainerLowest",
)

/**
 * Couples (texte, fond) du critère 2 de la spec, limités aux 48 rôles du schéma (les couples
 * de `reussi` et d'`alerte` viennent avec leurs couleurs, mission 007).
 */
fun couples(): List<Pair<String, String>> {
    val l = LinkedHashSet<Pair<String, String>>()
    fun on(x: String) = "on" + x.replaceFirstChar { it.uppercase() }
    for (x in listOf("primary", "primaryContainer", "secondary", "secondaryContainer", "tertiary",
        "tertiaryContainer", "error", "errorContainer", "background", "surface", "surfaceVariant")) {
        l += on(x) to x
    }
    l += "inverseOnSurface" to "inverseSurface"
    for (x in listOf("primary", "secondary", "tertiary")) {
        for (t in listOf("${on(x)}Fixed", "${on(x)}FixedVariant")) for (f in listOf("${x}Fixed", "${x}FixedDim")) l += t to f
    }
    for (t in listOf("onSurface", "onSurfaceVariant")) for (f in SURFACES) l += t to f
    for (t in listOf("primary", "secondary", "tertiary", "error")) for (f in SURFACES) l += t to f
    l += "inversePrimary" to "inverseSurface"
    return l.toList()
}

/** Couples du critère 2 propres aux couleurs d'état : `reussi.couleur` en texte, et chaque « sur ». */
fun couplesSemantiques(): List<Pair<String, String>> =
    SURFACES.map { "reussi.couleur" to it } +
        SEMANTIQUES.flatMap { x -> listOf("$x.surCouleur" to "$x.couleur", "$x.surConteneur" to "$x.conteneur") }

/**
 * Rapport tronqué à 3 décimales, jamais arrondi (spec). La tolérance de 1e-9 absorbe le bruit
 * flottant d'un rapport exact, comme dans les tests de la mission 005.
 */
fun tronque3(r: Double): Double = floor(r * 1000.0 + 1e-9) / 1000.0

/**
 * Rôles qui servent de texte dans un couple sous [Reglages.SEUIL_TEXTE], dans l'ordre de [ROLES]
 * puis de [NOMS_SEMANTIQUES]. Si [j] porte les jetons d'état, leurs couples sont contrôlés et
 * [INTERDITS_PAR_DECISION] s'ajoute.
 */
fun interditsEnTexte(j: Map<String, Long>): Set<String> {
    val etats = j.keys.containsAll(NOMS_SEMANTIQUES)
    val echecs = (couples() + if (etats) couplesSemantiques() else emptyList())
        .filter { (t, f) -> tronque3(Contraste.rapport(j.getValue(t), j.getValue(f))) < Reglages.SEUIL_TEXTE }
        .map { it.first }
        .toSet()
    val ordre = ROLES.map { it.first } + if (etats) NOMS_SEMANTIQUES else emptyList()
    return ordre.filterTo(LinkedHashSet()) { it in echecs || (etats && it in INTERDITS_PAR_DECISION) }
}

fun hex(v: Long): String = "0x" + java.lang.Long.toHexString(v).uppercase(Locale.ROOT).padStart(8, '0')

private fun fmt(d: Double) = String.format(Locale.ROOT, "%.3f", d)

/** Un écart de teinte, à 0,1° près. */
fun degres(d: Double): String = String.format(Locale.ROOT, "%.1f", d)

/** Les fichiers produits, chemin relatif au module -> contenu. */
fun fichiers(): Map<String, String> {
    val clair = tousLesJetons(sombre = false)
    val sombre = tousLesJetons(sombre = true)
    return linkedMapOf(
        "src/main/kotlin/fr/nemeton/terrain/style/StyleTerrain.kt" to sourceKotlin(clair, sombre),
        "src/test/resources/jetons/jetons.csv" to tableCsv(clair, sombre),
        "generation/nuancier.svg" to nuancier(clair, sombre),
    )
}

fun sourceKotlin(clair: Map<String, Long>, sombre: Map<String, Long>): String = buildString {
    val ic = interditsEnTexte(clair)
    val iso = interditsEnTexte(sombre)
    fun liste(s: Set<String>) = if (s.isEmpty()) "aucun" else s.joinToString(", ") { "`$it`" }
    appendLine("// Fichier généré par `./gradlew :terrain-style:generer` (terrain-style/generation/src).")
    appendLine("// Ne pas modifier à la main : la CI régénère et échoue sur toute différence.")
    appendLine("//")
    appendLine("// Material Color Utilities, sources Kotlin au commit ${Reglages.COMMIT_MCU}")
    appendLine("// Couleur de départ : ${hex(Reglages.SOURCE)} (${Reglages.NOM_SOURCE})")
    appendLine("// Variante : ${Reglages.VARIANTE} ; spec : ${Reglages.SPEC} (imposée par ${Reglages.VARIANTE} sur ${Reglages.PLATEFORME}, vérifiée)")
    appendLine("// Contraste : ${Reglages.CONTRASTE} ; plateforme : ${Reglages.PLATEFORME}")
    appendLine("// Réussi : ${hex(Reglages.REUSSI)}, ${Reglages.SOURCE_REUSSI}, en palette d'erreur (ADR 0002)")
    appendLine("// Écart de teinte HCT de reussi.couleur à primary : clair ${degres(ecartReussiPrimary(false))}°, sombre ${degres(ecartReussiPrimary(true))}°")
    appendLine("// Alerte : ${hex(Reglages.ALERTE)} et ${hex(Reglages.SUR_ALERTE)}, ${Reglages.SOURCE_ALERTE}, figée hors MCU (ADR 0002)")
    appendLine("package fr.nemeton.terrain.style")
    appendLine()
    appendLine("/**")
    appendLine(" * Les schémas communs des applications de terrain, générés par Material Color Utilities")
    appendLine(" * (${Reglages.NOM_SOURCE} ${hex(Reglages.SOURCE)}, ${Reglages.VARIANTE}, ${Reglages.SPEC}, contraste ${Reglages.CONTRASTE}).")
    appendLine(" *")
    appendLine(" * Rôles interdits en texte (sous ${Reglages.SEUIL_TEXTE}:1 sur l'un de leurs fonds, et `alerte.couleur`")
    appendLine(" * par décision, ADR 0002) :")
    appendLine(" *  - clair : ${liste(ic)} ;")
    appendLine(" *  - sombre : ${liste(iso)}.")
    appendLine(" */")
    appendLine("object StyleTerrain {")
    appendLine("    val clair: Schema = Schema(")
    schema(clair, ic)
    appendLine("    )")
    appendLine()
    appendLine("    val sombre: Schema = Schema(")
    schema(sombre, iso)
    appendLine("    )")
    appendLine("}")
}

private fun StringBuilder.schema(j: Map<String, Long>, interdits: Set<String>) {
    for ((nom, _) in ROLES) appendLine("        $nom = ${hex(j.getValue(nom))}L,")
    for (x in SEMANTIQUES) {
        val v = listOf("couleur", "surCouleur", "conteneur", "surConteneur").joinToString(", ") { "${hex(j.getValue("$x.$it"))}L" }
        appendLine("        $x = Semantique($v),")
    }
    appendLine("        interditsEnTexte = setOf(${interdits.joinToString(", ") { "\"$it\"" }}),")
}

fun tableCsv(clair: Map<String, Long>, sombre: Map<String, Long>): String = buildString {
    appendLine("theme,role,argb")
    for ((theme, j) in listOf("clair" to clair, "sombre" to sombre)) for ((nom, v) in j) appendLine("$theme,$nom,${hex(v)}")
}

/** Fond et texte des bandeaux d'état, erreur, réussi et alerte côte à côte. */
private val ETATS = listOf(
    "error" to "onError", "reussi.couleur" to "reussi.surCouleur", "alerte.couleur" to "alerte.surCouleur",
    "errorContainer" to "onErrorContainer", "reussi.conteneur" to "reussi.surConteneur", "alerte.conteneur" to "alerte.surConteneur",
)

/**
 * Nuancier SVG : la couleur de départ en tête, puis, clair à gauche et sombre à droite, les
 * bandeaux d'état (erreur, réussi, alerte, écrits dans leur couleur « sur ») et un bandeau par rôle.
 */
fun nuancier(clair: Map<String, Long>, sombre: Map<String, Long>): String = buildString {
    val larg = 300
    val haut = 24
    val marge = 10
    val entete = 3 * haut
    val etats = ETATS.size * haut + marge
    val l = 2 * larg + 3 * marge
    val h = entete + etats + ROLES.size * haut + 2 * marge
    fun rgb(v: Long) = "#" + java.lang.Long.toHexString(v and 0xFFFFFFL).uppercase(Locale.ROOT).padStart(6, '0')
    // Texte noir ou blanc, celui qui contraste le plus avec le bandeau.
    fun encre(v: Long) = if (Contraste.rapport(v, 0xFF000000L) >= Contraste.rapport(v, 0xFFFFFFFFL)) "#000000" else "#FFFFFF"
    appendLine("""<svg xmlns="http://www.w3.org/2000/svg" width="$l" height="$h" viewBox="0 0 $l $h" font-family="sans-serif" font-size="12">""")
    appendLine("""  <rect width="$l" height="$h" fill="#FFFFFF"/>""")
    val src = Reglages.SOURCE
    appendLine("""  <rect x="$marge" y="$marge" width="${2 * larg + marge}" height="$haut" fill="${rgb(src)}"/>""")
    appendLine("""  <text x="${marge + 6}" y="${marge + 16}" fill="${encre(src)}">${Reglages.NOM_SOURCE} ${rgb(src)} (couleur de départ) ; ${Reglages.VARIANTE}, ${Reglages.SPEC}, contraste ${Reglages.CONTRASTE}</text>""")
    for ((i, theme) in listOf("clair", "sombre").withIndex()) {
        appendLine("""  <text x="${marge + i * (larg + marge)}" y="${marge + haut + 18}" fill="#000000" font-weight="bold">$theme</text>""")
    }
    for ((i, pair) in listOf(clair to interditsEnTexte(clair), sombre to interditsEnTexte(sombre)).withIndex()) {
        val (j, interdits) = pair
        val x = marge + i * (larg + marge)
        for ((k, etat) in ETATS.withIndex()) {
            val (fond, texte) = etat
            val y = marge + entete + k * haut
            val note = if (fond in interdits) " (interdit en texte)" else ""
            appendLine("""  <rect x="$x" y="$y" width="$larg" height="$haut" fill="${rgb(j.getValue(fond))}"/>""")
            appendLine("""  <text x="${x + 6}" y="${y + 16}" fill="${rgb(j.getValue(texte))}">$fond ${rgb(j.getValue(fond))}$note</text>""")
        }
        for ((k, nom) in ROLES.map { it.first }.withIndex()) {
            val v = j.getValue(nom)
            val y = marge + entete + etats + k * haut
            val note = if (nom in interdits) " (interdit en texte)" else ""
            appendLine("""  <rect x="$x" y="$y" width="$larg" height="$haut" fill="${rgb(v)}"/>""")
            appendLine("""  <text x="${x + 6}" y="${y + 16}" fill="${encre(v)}">$nom ${rgb(v)}$note</text>""")
        }
    }
    appendLine("</svg>")
}

/** `args[0]` : répertoire du module `terrain-style`. */
fun main(args: Array<String>) {
    val module = File(args.getOrElse(0) { "." })
    for ((chemin, contenu) in fichiers()) {
        val f = File(module, chemin)
        f.parentFile.mkdirs()
        f.writeText(contenu, Charsets.UTF_8)
        println("écrit $chemin")
    }
    for ((theme, j) in listOf("clair" to tousLesJetons(false), "sombre" to tousLesJetons(true))) {
        for ((t, f) in couples() + couplesSemantiques()) {
            val r = tronque3(Contraste.rapport(j.getValue(t), j.getValue(f)))
            if (r < Reglages.SEUIL_TEXTE) println("ECHEC $theme $t sur $f ${hex(j.getValue(t))}/${hex(j.getValue(f))} rapport=${fmt(r)}")
        }
        println("interdits en texte ($theme) : ${interditsEnTexte(j)}")
        println("ECART $theme reussi.couleur / primary : ${degres(ecartReussiPrimary(theme == "sombre"))}° (teinte HCT)")
    }
}
