package domain.user.useCase

import Resource
import domain.user.repository.DatabaseRepository

class UnsubscribeUserFromShareUseCase(
    private val database: DatabaseRepository
) {
    suspend operator fun invoke(userId: Long, ticker: String): Resource<Unit> {
        return when (database.unsubscribeUserFromShare(userId, ticker)) {
            true -> Resource.Success(Unit)
            false -> Resource.Error("")
        }
    }
}