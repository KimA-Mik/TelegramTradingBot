package data.db

import data.db.entities.UserEntity
import data.db.mappers.toUser
import data.db.tables.Users
import domain.user.model.User
import domain.user.repository.UserRepository
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.jetbrains.exposed.v1.jdbc.SchemaUtils
import org.jetbrains.exposed.v1.jdbc.transactions.TransactionManager
import org.jetbrains.exposed.v1.jdbc.transactions.transaction
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
            val tables = arrayOf(Users)
            SchemaUtils.create(*tables)
//            val missingColumnsStatements =
//                MigrationUtils.statementsRequiredForDatabaseMigration(*tables)
//            missingColumnsStatements.forEach {
//                logger.info("Executing statement: $it")
//                try {
//                    connection.prepareStatement(it, true).executeUpdate()
//                } catch (e: Exception) {
//                    logger.error(e.message)
//                }
//            }
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
                it.ticker = user.ticker
                it.targetPrice = user.targetPrice
                it.isActive = user.isActive
                it.remainActive = user.remainActive
                it.targetDeviation = user.targetDeviation
                it.securityConfigureSequence = user.securityConfigureSequence
                it.note = user.note
                it.showNote = user.showNote
            }?.toUser()
        }
}