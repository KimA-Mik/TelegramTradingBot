package domain.user.repository

import domain.user.model.FullUser
import domain.user.model.TrackingSecurity
import domain.user.model.User

interface UserRepository {
    suspend fun findUserById(id: Long): User?
    suspend fun registerUser(id: Long): User
    suspend fun updateUser(user: User): User?
    suspend fun getAllUsers(): List<User>
    suspend fun findFullUserById(id: Long): FullUser?
    suspend fun getFullUsers(): List<FullUser>
    suspend fun createTrackingSecurity(user: User, security: TrackingSecurity): Result<TrackingSecurity>
    suspend fun updateTrackingSecurity(security: TrackingSecurity): Result<TrackingSecurity>
    suspend fun deleteTrackingSecurity(id: Long)
}