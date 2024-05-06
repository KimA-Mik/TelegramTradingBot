package domain.agent.useCase

import domain.agent.repositoty.AgentRepository

class GetAgentBotIdUseCase(
    private val repository: AgentRepository
) {
    operator fun invoke() = repository.getBotId()
}