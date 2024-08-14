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
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.statements.BatchUpdateStatement
import org.jetbrains.exposed.sql.statements.UpdateStatement
import org.jetbrains.exposed.sql.transactions.transaction
import org.slf4j.LoggerFactory

class DatabaseRepositoryImpl(
    private val database: DatabaseConnector
) : DatabaseRepository {
    private val logger = LoggerFactory.getLogger(this::class.java)

    init {
        transaction {
            SchemaUtils.create(Shares, Users, UserShares)

            val migrations = SchemaUtils.addMissingColumnsStatements(Shares, Users, UserShares)
            migrations.forEach { migration ->
                val statement = connection.prepareStatement(migration, false)
                statement.executeUpdate()
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

    override suspend fun findUserByAgentChatId(chatId: String): User? {
        return database.transaction {
            Users
                .selectAll()
                .where { Users.agentChatId eq chatId }
                .firstOrNull()
                ?.toUser()
        }
    }

    override suspend fun findUserByAgentCode(code: String): User? {
        return database.transaction {
            Users
                .selectAll()
                .where { Users.agentCode eq code }
                .firstOrNull()
                ?.toUser()
        }
    }

    override suspend fun updateUser(user: User): User {
        return database.transaction {
            Users.update({ Users.id eq user.id }) {
                it.updateUser(user)
            }

            return@transaction user
        }
    }

    override suspend fun subscribeUserToShare(userId: Long, defaultPercent: Double, share: TinkoffShare): Boolean {
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
            Shares
                .join(
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
                    UserShares.bollingerBandsNotified
                )
                .map { it.toFollowedShare() }
        }
    }

    override suspend fun updateUserSharePercent(userId: Long, userShare: UserShare): Boolean {
        return database.transaction {
            val ids = Shares
                .join(
                    UserShares, JoinType.INNER,
                    onColumn = Shares.id, otherColumn = UserShares.shareId,
                    additionalConstraint = { UserShares.userId eq userId }
                )
                .select(UserShares.id)
                .where { Shares.ticker eq userShare.ticker }
                .map { it[UserShares.id].value }

            if (ids.size != 1) {
                return@transaction false
            }

            val id = ids.first()
            val updated = UserShares.update({ UserShares.id eq id }) {
                it[percent] = userShare.percent
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
                    Users.agentChatId,
                    Users.agentNotifications,
                    UserShares.id,
                    Shares.name,
                    Shares.uid,
                    Shares.ticker,
                    UserShares.percent,
                    UserShares.notified,
                    UserShares.rsiNotified,
                    UserShares.bollingerBandsNotified
                )
                .groupBy { it[Users.id] }
                .map {
                    UserWithFollowedShares(
                        id = it.key,
                        agentChatId = it.value.firstOrNull()?.get(Users.agentChatId),
                        agentNotifications = it.value.firstOrNull()?.get(Users.agentNotifications) ?: false,
                        shares = it.value.map { row ->
                            row.toFollowedShare()
                        }
                    )
                }
        }
    }

    override suspend fun updateUserShares(userShares: List<UserShare>) {
        if (userShares.isEmpty()) return
        database.transaction {
            val statement = BatchUpdateStatement(UserShares)
            userShares.forEach {
                statement.addBatch(EntityID(id = it.id, UserShares))
                statement[UserShares.notified] = it.futuresNotified
                statement[UserShares.percent] = it.percent
                statement[UserShares.rsiNotified] = it.rsiNotified
                statement[UserShares.bollingerBandsNotified] = it.bollingerBandsNotified
            }

            try {
                statement.execute(this)
            } catch (e: Exception) {
                logger.info(e.message)
            }
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
            bollingerBandsNotified = this[UserShares.bollingerBandsNotified]
        )
    }

    private fun ResultRow.toUser(): User {
        return User(
            id = this[Users.id],
            registered = this[Users.registered],
            path = this[Users.path],
            defaultPercent = this[Users.defaultPercent],
            agentChatId = this[Users.agentChatId],
            agentCode = this[Users.agentCode],
            agentNotifications = this[Users.agentNotifications],
        )
    }

    private fun UpdateStatement.updateUser(user: User) {
        this[Users.id] = user.id
        this[Users.registered] = user.registered
        this[Users.path] = user.path
        this[Users.defaultPercent] = user.defaultPercent
        this[Users.agentChatId] = user.agentChatId
        this[Users.agentCode] = user.agentCode
        this[Users.agentNotifications] = user.agentNotifications
    }
}