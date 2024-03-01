package data.db.repository

import data.db.DatabaseConnector
import data.db.entities.*
import data.db.mappers.toUser
import domain.tinkoff.model.TinkoffShare
import domain.user.model.User
import domain.user.repository.DatabaseRepository
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.transactions.transaction

class DatabaseRepositoryImpl(
    private val database: DatabaseConnector
) : DatabaseRepository {
    init {
        transaction {
            SchemaUtils.create(Shares, Users, UserSecurities)
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

    override suspend fun subscribeUserToShare(userId: Long, share: TinkoffShare) {
        return database.transaction {
            val shareQuery = ShareEntity.find { Shares.ticker eq share.ticker }
            val actualShare = if (shareQuery.count() == 1L) {
                shareQuery.single()
            } else {
                ShareEntity.new {
                    this.name = share.name
                    this.lot = share.lot
                    this.uid = share.uid
                }
            }
            val existed =
                UserSecurityEntity.find { (UserSecurities.user eq userId) and (UserSecurities.security eq actualShare.id) }

            if (existed.count() > 0) return@transaction
            UserSecurityEntity.new {
                transaction { }
            }
        }
    }

}