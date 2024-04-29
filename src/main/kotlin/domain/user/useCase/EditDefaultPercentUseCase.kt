package domain.user.useCase

import domain.user.repository.DatabaseRepository

class EditDefaultPercentUseCase(
    private val userRepository: DatabaseRepository
) {
    suspend operator fun invoke(userId: Long, change: Double): Result {
        val user = userRepository.findUser(userId) ?: return Result.Error
        var newPercent = user.defaultPercent + change
        if (newPercent < 0.0) newPercent = 0.0

        val updatedUser = userRepository.updateUser(
            user.copy(defaultPercent = newPercent)
        ) ?: return Result.Error
        return Result.Success(updatedUser.defaultPercent)
    }

    sealed interface Result {
        data class Success(val newPercent: Double) : Result
        data object Error : Result
    }
}