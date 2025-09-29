package data.db

import data.db.entities.UserEntity
import data.db.mappers.toUser
import domain.user.model.User
import domain.user.repository.UserRepository
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toJavaLocalDateTime
import kotlinx.datetime.toLocalDateTime
import kotlin.time.ExperimentalTime

class UserRepositoryImpl(
    private val databaseConnector: DatabaseConnector
) : UserRepository {
    override suspend fun findUserById(id: Long): User? =
        databaseConnector.transaction { UserEntity.findById(id)?.toUser() }

    override suspend fun registerUser(id: Long): User =
        databaseConnector.transaction { UserEntity.new(id = id) {}.toUser() }

    @OptIn(ExperimentalTime::class)
    override suspend fun updateUser(user: User): User? =
        databaseConnector.transaction {
            UserEntity.findByIdAndUpdate(user.id) {
                it.path = user.path
                it.registered = user.registered
                    .toLocalDateTime(TimeZone.currentSystemDefault())
                    .toJavaLocalDateTime()
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