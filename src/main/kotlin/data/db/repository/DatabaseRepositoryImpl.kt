package data.db.repository

import data.db.DatabaseConnector
import data.db.entities.Shares
import data.db.entities.UserShares
import data.db.entities.Users
import domain.tinkoff.model.TinkoffShare
import domain.user.common.DEFAULT_SHARE_PERCENT
import domain.user.model.User
import domain.user.repository.DatabaseRepository
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction

class DatabaseRepositoryImpl(
    private val database: DatabaseConnector
) : DatabaseRepository {
    init {
        transaction {
            SchemaUtils.create(Shares, Users, UserShares)
        }
    }

    override suspend fun registerUser(id: Long) {
        database.transaction {
            Users.insert {
                it[this.id] = id
                it[path] = String()
            }
        }
    }

    override suspend fun findUser(id: Long): User? {
        return database.transaction {
            Users
                .selectAll()
                .where { Users.id eq id }
                .map {
                    User(
                        id = it[Users.id],
                        registered = it[Users.registered],
                        path = it[Users.path]
                    )
                }
                .firstOrNull()
        }
    }

    override suspend fun updateUser(user: User): User {
        return database.transaction {
            Users.update({ Users.id eq user.id }) {
                it[id] = user.id
                it[registered] = user.registered
                it[path] = user.path
            }

            return@transaction user
        }
    }

    override suspend fun subscribeUserToShare(userId: Long, share: TinkoffShare): Boolean {
        return database.transaction {
            val sharesList = Shares
                .select(Shares.id)
                .where { Shares.ticker eq share.ticker }
                .map { it[Shares.id].value }
                .toList()

            val shareId = if (sharesList.isNotEmpty()) {
                sharesList.first()
            } else {
                Shares.insertAndGetId {
                    it[name] = share.name
                    it[ticker] = share.ticker
                    it[lot] = share.lot
                    it[uid] = share.uid
                }.value
            }

            val existedLinks = UserShares
                .selectAll()
                .where { UserShares.userId eq userId }
                .andWhere { UserShares.shareId eq shareId }
                .toList()

            if (existedLinks.isNotEmpty()) return@transaction false

            UserShares.insert {
                it[UserShares.userId] = userId
                it[UserShares.shareId] = shareId
                it[percent] = DEFAULT_SHARE_PERCENT
            }

            true
        }
    }

    override suspend fun unsubscribeUserFromShare(userId: Long, ticker: String): Boolean {
        return database.transaction {
            val sharesList = Shares
                .select(Shares.id)
                .where { Shares.ticker eq ticker }
                .map { it[Shares.id].value }
                .toList()

            if (sharesList.isEmpty()) return@transaction false

            val shareId = sharesList.first()
            val deleted = UserShares.deleteWhere {
                (UserShares.shareId eq shareId) and (UserShares.userId eq userId)
            }

            return@transaction deleted > 0
        }
    }

    override suspend fun isUserSubscribed(userId: Long, ticker: String): Boolean {
        //TODO: Compress to one expression later
        return database.transaction {
            val sharesList = Shares
                .select(Shares.id)
                .where { Shares.ticker eq ticker }
                .map { it[Shares.id].value }
                .toList()

            if (sharesList.isEmpty()) return@transaction false
            val shareId = sharesList.first()

            val existedLinks = UserShares
                .selectAll()
                .where { UserShares.userId eq userId }
                .andWhere { UserShares.shareId eq shareId }
                .toList()

            return@transaction existedLinks.isNotEmpty()
        }
    }
}