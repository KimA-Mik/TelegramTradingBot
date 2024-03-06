package domain.user.useCase

import domain.user.model.UserShare
import domain.user.repository.DatabaseRepository

class GetUserSharesUseCase(private val repository: DatabaseRepository) {
    suspend operator fun invoke(userId: Long): List<UserShare> {
        return repository.getUserShares(userId)
    }

    companion object {
        const val PAGE_SIZE = 5
    }
}