package data.agent

class AgentService {
    private var id: String? = null
    fun setBotId(agentId: String) {
        id = agentId
    }

    fun getBotId(): String? {
        return id
    }
}