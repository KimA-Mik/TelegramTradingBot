package data.agent

import domain.agent.repositoty.AgentRepository

class AgentRepositoryImpl(
    private val service: AgentService
) : AgentRepository {
    override fun setBotId(id: String) {
        service.setBotId(id)
    }

    override fun setBotName(name: String) {
        service.setBotName(name)
    }

    override fun getBotId(): String? {
        return service.getBotId()
    }

    override fun genBotName(): String? {
        return service.genBotName()
    }
}