package domain.user.useCase

import domain.user.repository.DatabaseRepository

class IsUserSubscribedUseCase(val repository: DatabaseRepository) {
    suspend operator fun invoke(userId: Long, ticker: String): Boolean {
        return repository.isUserSubscribed(userId, ticker)
    }
}