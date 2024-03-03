package domain.user.repository

import domain.tinkoff.model.TinkoffShare
import domain.user.model.User

interface DatabaseRepository {
    suspend fun registerUser(id: Long)
    suspend fun findUser(id: Long): User?
    suspend fun updateUser(user: User): User?
    suspend fun subscribeUserToShare(userId: Long, share: TinkoffShare): Boolean
    suspend fun unsubscribeUserToShare(userId: Long, ticker: String): Boolean
}