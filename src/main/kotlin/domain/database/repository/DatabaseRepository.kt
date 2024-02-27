package domain.database.repository

interface DatabaseRepository {
    suspend fun crateUser(id: Long): Boolean
}