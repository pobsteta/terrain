package fr.nemeton.terrain.gpkg

/**
 * Accès SQLite minimal dont le GeoPackage a besoin, à brancher sur `android.database.sqlite` dans
 * l'application et sur sqlite-jdbc dans les tests JVM : pas de fonction spatiale (`ST_*`), du SQL
 * simple, donc le même code sur l'appareil et sur le poste.
 *
 * Valeurs des lignes : `Long`, `Double`, `String`, `ByteArray` ou null, comme SQLite les stocke.
 */
interface BaseSqlite {
    fun executer(sql: String, args: List<Any?> = emptyList())

    fun requete(sql: String, args: List<Any?> = emptyList()): List<Map<String, Any?>>

    /** `BEGIN IMMEDIATE` … `COMMIT`, `ROLLBACK` en cas d'erreur. */
    fun <T> transactionImmediate(bloc: () -> T): T
}
