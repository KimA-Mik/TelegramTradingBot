package domain.agent.useCase

import domain.agent.repositoty.AgentRepository

class SetAgentBotInfoUseCase(
    private val repository: AgentRepository
) {
    operator fun invoke(botId: String, botName: String) {
        repository.setBotId(botId)
        repository.setBotName(botName)
    }
}