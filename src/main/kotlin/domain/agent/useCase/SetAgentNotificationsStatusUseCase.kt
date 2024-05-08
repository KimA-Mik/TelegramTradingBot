package domain.agent.useCase

import domain.user.model.User
import domain.user.repository.DatabaseRepository

class SetAgentNotificationsStatusUseCase(
    private val repository: DatabaseRepository
) {
    suspend operator fun invoke(userId: Long, status: Boolean): Result {
        val user = repository.findUser(userId)
            ?: return Result.Error

        if (status && user.agentChatId == null)
            return Result.NoAgent

        val updated = user.copy(
            agentNotifications = status
        )
        val res = repository.updateUser(updated)
            ?: return Result.Error

        return Result.Success(res)
    }

    sealed interface Result {
        data object Error : Result
        data object NoAgent : Result
        data class Success(val user: User) : Result
    }
}