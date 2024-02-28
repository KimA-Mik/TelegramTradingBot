package data.db.repository

import data.db.DatabaseConnector
import data.db.entities.Securities
import data.db.entities.UserEntity
import data.db.entities.UserSecurities
import data.db.entities.Users
import data.db.mappers.toUser
import domain.user.model.User
import domain.user.repository.DatabaseRepository
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction

class DatabaseRepositoryImpl(
    private val database: DatabaseConnector
) : DatabaseRepository {
    init {
        transaction {
            SchemaUtils.create(Securities, Users, UserSecurities)
        }
    }

    override suspend fun registerUser(id: Long) {
        database.transaction {
            UserEntity.new(id) {
                path = String()
            }
        }
    }

    override suspend fun findUser(id: Long): User? {
        return database.transaction {
            UserEntity.findById(id)
        }?.toUser()
    }

    override suspend fun updateUser(user: User): User? {
        return database.transaction {
            UserEntity.findByIdAndUpdate(user.id) {
                it.path = user.path
            }
        }?.toUser()
    }

}