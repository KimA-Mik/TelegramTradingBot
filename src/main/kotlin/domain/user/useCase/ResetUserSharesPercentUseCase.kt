package domain.user.useCase

import domain.user.model.User
import domain.user.repository.DatabaseRepository

class ResetUserSharesPercentUseCase(
    private val databaseRepository: DatabaseRepository
) {
    suspend operator fun invoke(user: User): Result {
        try {
            val userShares = databaseRepository
                .getUserShares(userId = user.id)
                .map { it.copy(percent = user.defaultPercent) }

            if (userShares.isEmpty()) {
                return Result.Empty
            }

            databaseRepository.updateUserShares(userShares)
            return Result.Success(userShares.size)
        } catch (e: Exception) {
            return Result.Error
        }
    }

    sealed interface Result {
        data class Success(val count: Int) : Result
        data object Empty : Result
        data object Error : Result
    }
}