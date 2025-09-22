package domain.user.useCase

import domain.user.model.User
import domain.user.model.UserShare
import domain.user.repository.DatabaseRepository

class TransformUserSharesUseCase(
    private val repository: DatabaseRepository
) {
    suspend operator fun invoke(
        user: User,
        transformation: (UserShare) -> UserShare
    ): Boolean {
        val userShares = repository.getUserShares(user.id)
        if (userShares.isEmpty()) return false

        val updated = userShares.map(transformation)
        repository.updateUserShares(updated)

        return true
    }
}