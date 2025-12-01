package data.db

import kotlinx.coroutines.Dispatchers
import org.jetbrains.exposed.v1.core.Transaction
import org.jetbrains.exposed.v1.jdbc.Database
import org.jetbrains.exposed.v1.jdbc.transactions.experimental.newSuspendedTransaction
import org.jetbrains.exposed.v1.jdbc.transactions.transactionManager
import java.sql.Connection

class DatabaseConnector {
    private val connection: Database = Database.connect("jdbc:sqlite:data.db", "org.sqlite.JDBC")

    init {
        connection.transactionManager.defaultIsolationLevel = Connection.TRANSACTION_SERIALIZABLE
    }

    suspend fun <T> transaction(block: suspend Transaction.() -> T): T =
        newSuspendedTransaction(Dispatchers.IO, db = connection) { block() }

    suspend fun <T> transactionCatching(block: suspend Transaction.() -> T): Result<T> =
        runCatching { transaction { block() } }

}