package domain.agent.useCase

import domain.user.repository.DatabaseRepository
import kotlin.random.Random

class CreateAgentLinkRequestUseCase(
    private val repository: DatabaseRepository
) {
    suspend operator fun invoke(userId: Long): Result {
        val user = repository.findUser(userId) ?: return Result.Error

        var code = generateCode()
        while (repository.findUserByAgentCode(code) != null) {
            code = generateCode()
        }

        val newUser = user.copy(agentCode = code)
        val updated = repository.updateUser(newUser) ?: return Result.Error

        return if (updated.agentCode != null) {
            Result.Success(updated.agentCode)
        } else {
            Result.Error
        }
    }

    sealed interface Result {
        data class Success(val code: String) : Result
        data object Error : Result
    }

    private fun generateCode(): String {
        val tail = List(7) { Random.nextInt(10) }
            .joinToString(separator = "")

        val leading = (Random.nextInt(9) + 1).toString()
        return leading + tail
    }
}