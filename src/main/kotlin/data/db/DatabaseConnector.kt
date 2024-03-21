package data.db

import kotlinx.coroutines.Dispatchers
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.Transaction
import org.jetbrains.exposed.sql.transactions.TransactionManager
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import java.sql.Connection

class DatabaseConnector {
    private val connection: Database = Database.connect("jdbc:sqlite:data.db", "org.sqlite.JDBC")

    init {
        TransactionManager.manager.defaultIsolationLevel = Connection.TRANSACTION_SERIALIZABLE
    }

    suspend fun <T> transaction(block: suspend Transaction.() -> T): T =
        newSuspendedTransaction(Dispatchers.IO, db = connection) { block() }

}