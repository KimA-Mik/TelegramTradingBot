package domain.agent.useCase

import domain.user.model.User
import domain.user.repository.DatabaseRepository

class LinkAgentAccountUseCase(
    private val repository: DatabaseRepository
) {
    suspend operator fun invoke(chatId: String, code: String): Result {
        if (repository.findUserByAgentChatId(chatId) != null)
            return Result.AlreadyLinked

        val user = repository.findUserByAgentCode(code)
            ?: return Result.NoRequest

        val linkedUser = user.copy(
            agentChatId = chatId,
            agentCode = null
        )

        val updated = repository.updateUser(linkedUser)
            ?: return Result.Error

        return Result.Success(updated)
    }

    sealed interface Result {
        data object Error : Result
        data object NoRequest : Result
        data object AlreadyLinked : Result
        data class Success(val user: User) : Result
    }
}