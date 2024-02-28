package domain.user.repository

import domain.user.model.User

interface DatabaseRepository {
    suspend fun registerUser(id: Long)
    suspend fun findUser(id: Long): User?
    suspend fun updateUser(user: User): User?
}