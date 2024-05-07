package domain.agent.useCase

import domain.agent.model.AgentBotInfo
import domain.agent.repositoty.AgentRepository

class GetAgentBotIdUseCase(
    private val repository: AgentRepository
) {
    operator fun invoke(): AgentBotInfo? {
        val id = repository.getBotId() ?: return null
        val name = repository.genBotName() ?: return null

        return AgentBotInfo(id, name)
    }
}