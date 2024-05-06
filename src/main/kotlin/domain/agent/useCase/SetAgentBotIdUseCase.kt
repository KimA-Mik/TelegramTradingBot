package domain.agent.useCase

import domain.agent.repositoty.AgentRepository

class SetAgentBotIdUseCase(
    private val repository: AgentRepository
) {
    operator fun invoke(botId: String) {
        repository.setBotId(botId)
    }
}