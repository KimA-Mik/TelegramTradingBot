package data.db.repository

import data.db.DatabaseConnector
import data.db.entities.Shares
import data.db.entities.UserShares
import data.db.entities.Users
import domain.tinkoff.model.TinkoffShare
import domain.updateService.model.UserWithFollowedShares
import domain.user.model.User
import domain.user.model.UserShare
import domain.user.repository.DatabaseRepository
import org.jetbrains.exposed.v1.core.JoinType
import org.jetbrains.exposed.v1.core.ResultRow
import org.jetbrains.exposed.v1.core.and
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.core.statements.UpdateStatement
import org.jetbrains.exposed.v1.jdbc.*
import org.jetbrains.exposed.v1.jdbc.transactions.transaction
import org.jetbrains.exposed.v1.migration.jdbc.MigrationUtils
import org.slf4j.LoggerFactory

class DatabaseRepositoryImpl(
    private val database: DatabaseConnector
) : DatabaseRepository {
    private val logger = LoggerFactory.getLogger(this::class.java)

    init {
        transaction {
            val tables = arrayOf(Shares, Users, UserShares)
            SchemaUtils.create(*tables)
            val missingColumnsStatements =
                MigrationUtils.statementsRequiredForDatabaseMigration(*tables)
            missingColumnsStatements.forEach {
                logger.info("Executing statement: $it")
                try {
                    connection.prepareStatement(it, false).executeUpdate()
                } catch (e: Exception) {
                    logger.error(e.message)
                }
            }
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
                .firstOrNull()
                ?.toUser()
        }
    }

    override suspend fun updateUser(user: User): User? {
        return database.transaction {
            val updated = Users.update({ Users.id eq user.id }) {
                it.updateUser(user)
            }

            return@transaction if (updated > 0) user else null
        }
    }

    override suspend fun subscribeUserToShare(
        userId: Long,
        defaultPercent: Double,
        share: TinkoffShare
    ): Boolean {
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
                it[percent] = defaultPercent
                it[notified] = false
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

    override suspend fun getUserShares(userId: Long): List<UserShare> {
        return database.transaction {
            Shares.join(
                UserShares, JoinType.INNER,
                onColumn = Shares.id, otherColumn = UserShares.shareId,
                additionalConstraint = { UserShares.userId eq userId }
            )
                .select(
                    UserShares.id,
                    Shares.uid,
                    Shares.ticker,
                    Shares.name,
                    UserShares.percent,
                    UserShares.notified,
                    UserShares.rsiNotified,
                    UserShares.bollingerBandsNotified,
                    UserShares.rsiNotificationsEnabled,
                    UserShares.bbNotificationsEnabled
                )
                .map { it.toFollowedShare() }
                .toList()
        }
    }

    override suspend fun updateUserShare(userShare: UserShare): Boolean {
        return database.transaction {
            val updated = UserShares.update({ UserShares.id eq userShare.id }) {
                it.updateUserShare(userShare)
            }

            return@transaction updated > 0
        }
    }

    override suspend fun getUsersWithShares(): List<UserWithFollowedShares> {
        return database.transaction {
            Users
                .join(
                    UserShares, JoinType.LEFT,
                    onColumn = Users.id, otherColumn = UserShares.userId
                )
                .join(
                    Shares, JoinType.INNER,
                    onColumn = UserShares.shareId, otherColumn = Shares.id
                )
                .select(
                    Users.id,

                    UserShares.id,
                    Shares.name,
                    Shares.uid,
                    Shares.ticker,
                    UserShares.percent,
                    UserShares.notified,
                    UserShares.rsiNotified,
                    UserShares.bollingerBandsNotified,
                    UserShares.rsiNotificationsEnabled,
                    UserShares.bbNotificationsEnabled
                )
                .groupBy(Users.id)// { it[Users.id] }
                .map {
                    val result = mutableMapOf<Long, MutableList<UserShare>>()
                    val userShare = it.toFollowedShare()
                    val id = it[Users.id]
                    if (!result.contains(id)) result[id] = mutableListOf(userShare)
                    else result[id]!!.add(userShare)
                    result
                }
                .map { mapEntry ->
                    mapEntry.map {
                        UserWithFollowedShares(
                            id = it.key,
                            shares = it.value
                        )
                    }
                }.firstOrNull() ?: emptyList()
        }
    }

    override suspend fun updateUserShares(userShares: List<UserShare>) {
        if (userShares.isEmpty()) return
        database.transaction {
            UserShares.batchInsert(userShares) {
                this[UserShares.id] = it.id
                this[UserShares.notified] = it.futuresNotified
                this[UserShares.percent] = it.percent
                this[UserShares.rsiNotified] = it.rsiNotified
                this[UserShares.bollingerBandsNotified] = it.bollingerBandsNotified
                this[UserShares.rsiNotificationsEnabled] = it.rsiNotificationsEnabled
                this[UserShares.bbNotificationsEnabled] = it.bbNotificationsEnabled
            }
//            val statement = BatchUpdateStatement(UserShares)
//            userShares.forEach {
//                statement.addBatch(EntityID(id = it.id, UserShares))
//                statement[UserShares.notified] = it.futuresNotified
//                statement[UserShares.percent] = it.percent
//                statement[UserShares.rsiNotified] = it.rsiNotified
//                statement[UserShares.bollingerBandsNotified] = it.bollingerBandsNotified
//                statement[UserShares.rsiNotificationsEnabled] = it.rsiNotificationsEnabled
//                statement[UserShares.bbNotificationsEnabled] = it.bbNotificationsEnabled
//            }

//            try {
//                statement.execute(this)
//            } catch (e: Exception) {
//                logger.info(e.message)
//            }
        }
    }

    private fun ResultRow.toFollowedShare(): UserShare {
        return UserShare(
            id = this[UserShares.id].value,
            uid = this[Shares.uid],
            ticker = this[Shares.ticker],
            name = this[Shares.name],
            percent = this[UserShares.percent],
            futuresNotified = this[UserShares.notified],
            rsiNotified = this[UserShares.rsiNotified],
            bollingerBandsNotified = this[UserShares.bollingerBandsNotified],
            rsiNotificationsEnabled = this[UserShares.rsiNotificationsEnabled],
            bbNotificationsEnabled = this[UserShares.bbNotificationsEnabled],
        )
    }

    private fun ResultRow.toUser(): User {
        return User(
            id = this[Users.id],
            registered = this[Users.registered],
            path = this[Users.path],
            defaultPercent = this[Users.defaultPercent],
            defaultRsiNotifications = this[Users.defaultRsiNotifications],
            defaultBbNotifications = this[Users.defaultBbNotifications]
        )
    }

    private fun UpdateStatement.updateUser(user: User) {
        this[Users.id] = user.id
        this[Users.registered] = user.registered
        this[Users.path] = user.path
        this[Users.defaultPercent] = user.defaultPercent
        this[Users.defaultRsiNotifications] = user.defaultRsiNotifications
        this[Users.defaultBbNotifications] = user.defaultBbNotifications
    }

    private fun UpdateStatement.updateUserShare(userShare: UserShare) {
        this[UserShares.percent] = userShare.percent
        this[UserShares.notified] = userShare.futuresNotified
        this[UserShares.rsiNotified] = userShare.rsiNotified
        this[UserShares.bollingerBandsNotified] = userShare.bollingerBandsNotified
        this[UserShares.rsiNotificationsEnabled] = userShare.rsiNotificationsEnabled
        this[UserShares.bbNotificationsEnabled] = userShare.bbNotificationsEnabled
    }
}