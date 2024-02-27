package data.db.repository

import domain.database.repository.DatabaseRepository
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.transactions.TransactionManager
import java.sql.Connection

class DatabaseRepositoryImpl : DatabaseRepository {
    init {
        Database.connect("jdbc:sqlite:data.db", "org.sqlite.JDBC")
        TransactionManager.manager.defaultIsolationLevel =
            Connection.TRANSACTION_SERIALIZABLE

    }

    override suspend fun crateUser(id: Long): Boolean {
        return false
    }
}