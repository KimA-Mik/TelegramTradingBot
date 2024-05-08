package domain.agent.useCase

import domain.user.model.User
import domain.user.repository.DatabaseRepository

class UnlinkAgentUseCase(
    private val repository: DatabaseRepository
) {
    suspend operator fun invoke(userId: Long): Result {
        val user = repository.findUser(userId) ?: return Result.Error
        val updatedUser = user.copy(
            agentCode = null,
            agentChatId = null,
            agentNotifications = false
        )

        val res = repository.updateUser(updatedUser)

        return if (res != null) {
            Result.Success(res)
        } else {
            Result.Error
        }
    }

    sealed interface Result {
        data class Success(val user: User) : Result
        data object Error : Result
    }
}