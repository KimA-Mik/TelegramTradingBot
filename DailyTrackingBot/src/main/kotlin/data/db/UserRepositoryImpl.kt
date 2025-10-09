package data.db

import data.db.entities.SecurityEntity
import data.db.entities.UserEntity
import data.db.mappers.toFullUser
import data.db.mappers.toTrackingSecurity
import data.db.mappers.toUser
import data.db.tables.Securities
import data.db.tables.Users
import domain.user.model.FullUser
import domain.user.model.TrackingSecurity
import domain.user.model.User
import domain.user.repository.UserRepository
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.jetbrains.exposed.v1.dao.load
import org.jetbrains.exposed.v1.dao.with
import org.jetbrains.exposed.v1.jdbc.SchemaUtils
import org.jetbrains.exposed.v1.jdbc.transactions.TransactionManager
import org.jetbrains.exposed.v1.jdbc.transactions.transaction
import org.jetbrains.exposed.v1.migration.jdbc.MigrationUtils
import org.slf4j.LoggerFactory
import java.sql.Connection
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

class UserRepositoryImpl(
    private val databaseConnector: DatabaseConnector
) : UserRepository {
    private val logger = LoggerFactory.getLogger(this::class.java)

    init {
        TransactionManager.manager.defaultIsolationLevel = Connection.TRANSACTION_SERIALIZABLE
        transaction {
            val tables = arrayOf(Users, Securities)
            SchemaUtils.create(*tables)
            val missingColumnsStatements =
                MigrationUtils.statementsRequiredForDatabaseMigration(*tables)
            missingColumnsStatements.forEach {
                logger.info("Executing statement: $it")
                try {
                    connection.prepareStatement(it, true).executeUpdate()
                } catch (e: Exception) {
                    logger.error(e.message)
                }
            }
        }
    }

    override suspend fun findUserById(id: Long): User? =
        databaseConnector.transaction { UserEntity.findById(id)?.toUser() }

    @OptIn(ExperimentalTime::class)
    override suspend fun registerUser(id: Long): User =
        databaseConnector.transaction {
            UserEntity.new(id = id) {
                registered = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
            }.toUser()
        }

    @OptIn(ExperimentalTime::class)
    override suspend fun updateUser(user: User): User? =
        databaseConnector.transaction {
            UserEntity.findByIdAndUpdate(user.id) {
                it.path = user.path
                it.registered = user.registered.toLocalDateTime(TimeZone.currentSystemDefault())
            }?.toUser()
        }

    override suspend fun getAllUsers(): List<User> = databaseConnector.transaction {
        UserEntity.all().map { it.toUser() }
    }

    override suspend fun findFullUserById(id: Long): FullUser? = databaseConnector.transaction {
        UserEntity.findById(id)
            ?.load(UserEntity::securities)
            ?.toFullUser()
    }

    override suspend fun getFullUsers(): List<FullUser> = databaseConnector.transaction {
        UserEntity.all()
            .with(UserEntity::securities)
            .map { it.toFullUser() }
    }

    override suspend fun createTrackingSecurity(
        user: User, security: TrackingSecurity
    ): Result<TrackingSecurity> = databaseConnector.transactionCatching {
        SecurityEntity.new {
            this.user = UserEntity[user.id]
            ticker = security.ticker
            name = security.name
            uid = security.uid
            targetPrice = security.targetPrice
            targetDeviation = security.targetDeviation
            isActive = security.isActive
            remainActive = security.remainActive
            note = security.note
            showNote = security.showNote
            shouldNotify = security.shouldNotify
            shouldNotifyRsi = security.shouldNotifyRsi
            type = security.type
        }.toTrackingSecurity()
    }

    override suspend fun updateTrackingSecurity(security: TrackingSecurity) = databaseConnector.transactionCatching {
        SecurityEntity.findByIdAndUpdate(security.id) {
            updateSecurity(it, security)
//            it.ticker = security.ticker
//            it.name = security.name
//            it.uid = security.uid
//            it.targetPrice = security.targetPrice
//            it.targetDeviation = security.targetDeviation
//            it.isActive = security.isActive
//            it.remainActive = security.remainActive
//            it.note = security.note
//            it.showNote = security.showNote
//            it.shouldNotify = security.shouldNotify
//            it.shouldNotifyRsi = security.shouldNotifyRsi
//            it.type = security.type
        }!!.toTrackingSecurity()
    }

    override suspend fun updateTrackingSecurities(
        securities: List<TrackingSecurity>
    ): Result<List<TrackingSecurity>> = databaseConnector.transactionCatching {
        val result = mutableListOf<TrackingSecurity>()
        securities.forEach { security ->
            val newSecurity = SecurityEntity.findByIdAndUpdate(security.id) {
                updateSecurity(it, security)
            }!!.toTrackingSecurity()
            result.add(newSecurity)
        }
        return@transactionCatching result
    }

    private fun updateSecurity(entity: SecurityEntity, security: TrackingSecurity) {
        entity.ticker = security.ticker
        entity.name = security.name
        entity.uid = security.uid
        entity.targetPrice = security.targetPrice
        entity.targetDeviation = security.targetDeviation
        entity.isActive = security.isActive
        entity.remainActive = security.remainActive
        entity.note = security.note
        entity.showNote = security.showNote
        entity.shouldNotify = security.shouldNotify
        entity.shouldNotifyRsi = security.shouldNotifyRsi
        entity.type = security.type
    }

    override suspend fun deleteTrackingSecurity(id: Long) {
        databaseConnector.transaction {
            SecurityEntity.findById(id)?.delete()
        }
    }
}