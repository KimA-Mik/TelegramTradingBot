package domain.user.repository

import domain.user.model.User

interface UserRepository {
    suspend fun findUserById(id: Long): User?
    suspend fun registerUser(id: Long): User
    suspend fun updateUser(user: User): User?
    suspend fun getAllUsers(): List<User>
}