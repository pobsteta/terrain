package fr.nemeton.terrain.gpkg

import fr.nemeton.terrain.geo.Position

/** Couche vecteur d'un GeoPackage (`gpkg_contents` de type `features`). */
data class Couche(
    val table: String,
    val colonneGeometrie: String,
    val colonneId: String,
    val srsId: Int,
    /** Null si le système n'est pas géré : lire ses entités lève [SystemeNonGere]. */
    val systeme: Systeme?,
    /** `organisation:code` du système, pour les messages. */
    val descriptionSysteme: String,
)

/**
 * Entité d'une couche : identifiant (clé primaire), géométrie dans le système de la couche (null si
 * vide ou absente) et attributs **non nuls** en texte (`BOOLEAN` → `true` / `false`, les blobs
 * sont écartés).
 */
data class Entite(val id: Long, val geometrie: Geometrie?, val attributs: Map<String, String>) {
    /** Anneaux des polygones, en WGS 84. */
    fun anneauxWgs84(systeme: Systeme): List<List<Position>> = geometrie?.anneaux().orEmpty().map { a -> a.map(systeme::versWgs84) }

    /** Lignes (et contours des polygones), en WGS 84. */
    fun lignesWgs84(systeme: Systeme): List<List<Position>> = geometrie?.lignes().orEmpty().map { l -> l.map(systeme::versWgs84) }
}

/**
 * Lecture et écriture des couches vecteur d'un GeoPackage, en SQL simple sur une [BaseSqlite].
 * Les tables de tuiles (ortho) ne sont pas traitées ici.
 */
class GeoPackage(private val base: BaseSqlite) {

    /** Couches vecteur, dans l'ordre de `gpkg_contents`. */
    fun couches(): List<Couche> =
        base.requete(
            """
            SELECT c.table_name AS t, g.column_name AS g, g.srs_id AS s, r.organization AS o, r.organization_coordsys_id AS n
            FROM gpkg_contents c
            JOIN gpkg_geometry_columns g ON g.table_name = c.table_name
            LEFT JOIN gpkg_spatial_ref_sys r ON r.srs_id = g.srs_id
            WHERE c.data_type = 'features'
            ORDER BY c.rowid
            """.trimIndent(),
        ).map { r ->
            val table = r["t"] as String
            val srs = (r["s"] as Number).toInt()
            val organisation = r["o"] as String?
            val code = (r["n"] as Number?)?.toInt()
            Couche(
                table = table,
                colonneGeometrie = r["g"] as String,
                colonneId = colonnes(table).firstOrNull { it.cle }?.nom ?: "fid",
                srsId = srs,
                systeme = Systeme.de(organisation, code),
                descriptionSysteme = "${organisation ?: "?"}:${code ?: srs}",
            )
        }

    fun couche(table: String): Couche? = couches().firstOrNull { it.table.equals(table, ignoreCase = true) }

    /** Entités de [couche] ; lève [SystemeNonGere] si son système n'est pas géré. */
    fun entites(couche: Couche): List<Entite> {
        if (couche.systeme == null) throw SystemeNonGere(couche.table, couche.descriptionSysteme)
        val booleens = colonnes(couche.table).filter { it.type.equals("BOOLEAN", ignoreCase = true) }.map { it.nom }.toSet()
        return base.requete("SELECT * FROM ${ident(couche.table)}").map { ligne ->
            val blob = ligne[couche.colonneGeometrie] as ByteArray?
            val attributs = ligne
                .filterKeys { it != couche.colonneGeometrie }
                .mapNotNull { (k, v) ->
                    when {
                        v == null || v is ByteArray -> null
                        k in booleens && v is Number -> k to (v.toLong() != 0L).toString()
                        else -> k to v.toString()
                    }
                }.toMap()
            Entite(
                id = (ligne[couche.colonneId] as Number).toLong(),
                geometrie = blob?.let { FormatGpkg.decoder(it) },
                attributs = attributs,
            )
        }
    }

    /**
     * Ajoute une entité à [couche] dans une transaction courte (`BEGIN IMMEDIATE`) : [geometrie]
     * est dans le système de la couche (voir [Systeme.depuisWgs84]) ; renvoie son identifiant.
     *
     * Les déclencheurs de l'index spatial (`rtree_<table>_<géométrie>_*`, écrits par GDAL et QGIS)
     * appellent `ST_IsEmpty`, `ST_MinX`…, absentes du SQLite d'Android : ils sont mis de côté
     * le temps de l'insertion, la ligne d'index est écrite ici, puis ils sont recréés tels quels,
     * dans la même transaction (comme Groma). Les autres déclencheurs (métier) jouent normalement.
     */
    fun inserer(couche: Couche, geometrie: Geometrie, attributs: Map<String, Any?> = emptyMap()): Long =
        base.transactionImmediate {
            val rtree = "rtree_${couche.table}_${couche.colonneGeometrie}"
            val declencheurs = base.requete(
                "SELECT name, sql FROM sqlite_master WHERE type = 'trigger' AND tbl_name = ?",
                listOf(couche.table),
            ).filter { (it["name"] as String).startsWith(rtree + "_") }
            declencheurs.forEach { base.executer("DROP TRIGGER ${ident(it["name"] as String)}") }
            val noms = listOf(couche.colonneGeometrie) + attributs.keys
            base.executer(
                "INSERT INTO ${ident(couche.table)} (${noms.joinToString { ident(it) }}) VALUES (${noms.joinToString { "?" }})",
                listOf(FormatGpkg.encoder(geometrie, couche.srsId)) + attributs.values,
            )
            val id = (base.requete("SELECT last_insert_rowid() AS id").single()["id"] as Number).toLong()
            val points = geometrie.points()
            if (points.isNotEmpty() && base.requete("SELECT 1 AS un FROM sqlite_master WHERE name = ?", listOf(rtree)).isNotEmpty()) {
                base.executer(
                    "INSERT OR REPLACE INTO ${ident(rtree)} VALUES (?, ?, ?, ?, ?)",
                    listOf(id, points.minOf { it.x }, points.maxOf { it.x }, points.minOf { it.y }, points.maxOf { it.y }),
                )
            }
            declencheurs.forEach { base.executer(it["sql"] as String) }
            base.executer(
                "UPDATE gpkg_contents SET last_change = strftime('%Y-%m-%dT%H:%M:%fZ', 'now') WHERE table_name = ?",
                listOf(couche.table),
            )
            id
        }

    private data class Colonne(val nom: String, val type: String, val cle: Boolean)

    private fun colonnes(table: String): List<Colonne> =
        base.requete("PRAGMA table_info(${ident(table)})").map {
            Colonne(it["name"] as String, (it["type"] as String?).orEmpty(), ((it["pk"] as Number?)?.toInt() ?: 0) == 1)
        }

    private fun ident(nom: String) = "\"" + nom.replace("\"", "\"\"") + "\""
}
