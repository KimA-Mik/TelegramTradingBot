package domain.local.repository

import domain.local.model.User

interface DatabaseRepository {
    suspend fun registerUser(id: Long)
    suspend fun findUser(id: Long): User?
    suspend fun updateUser(user: User): User?
}