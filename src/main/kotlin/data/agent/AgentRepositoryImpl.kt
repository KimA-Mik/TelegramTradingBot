package data.agent

import domain.agent.repositoty.AgentRepository

class AgentRepositoryImpl(
    private val service: AgentService
) : AgentRepository {
    override fun setBotId(agentId: String) {
        service.setBotId(agentId)
    }

    override fun getBotId(): String? {
        return service.getBotId()
    }
}