package fr.nemeton.terrain.gpkg

import java.sql.Connection
import java.sql.DriverManager

/** [BaseSqlite] sur sqlite-jdbc, pour les tests (même contrat que l'adaptateur Android). */
class SqliteJdbc(chemin: String) : AutoCloseable {
    val connexion: Connection = DriverManager.getConnection("jdbc:sqlite:$chemin")

    val base = object : BaseSqlite {
        override fun executer(sql: String, args: List<Any?>) {
            connexion.prepareStatement(sql).use { st ->
                args.forEachIndexed { i, v -> st.setObject(i + 1, v) }
                st.execute()
            }
        }

        override fun requete(sql: String, args: List<Any?>): List<Map<String, Any?>> =
            connexion.prepareStatement(sql).use { st ->
                args.forEachIndexed { i, v -> st.setObject(i + 1, v) }
                st.executeQuery().use { rs ->
                    val meta = rs.metaData
                    buildList {
                        while (rs.next()) {
                            add(
                                (1..meta.columnCount).associate { c ->
                                    // Valeurs normalisées comme un curseur Android : Long, Double, String, ByteArray.
                                    meta.getColumnLabel(c) to when (val v = rs.getObject(c)) {
                                        is Int -> v.toLong()
                                        is Float -> v.toDouble()
                                        else -> v
                                    }
                                },
                            )
                        }
                    }
                }
            }

        override fun <T> transactionImmediate(bloc: () -> T): T {
            connexion.createStatement().use { it.execute("BEGIN IMMEDIATE") }
            return try {
                bloc().also { connexion.createStatement().use { it.execute("COMMIT") } }
            } catch (e: Throwable) {
                connexion.createStatement().use { it.execute("ROLLBACK") }
                throw e
            }
        }
    }

    override fun close() = connexion.close()
}
