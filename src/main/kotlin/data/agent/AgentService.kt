package data.agent

class AgentService {
    private var _id: String? = null
    private var _name: String? = null

    fun setBotId(agentId: String) {
        _id = agentId
    }

    fun setBotName(name: String) {
        _name = name
    }

    fun getBotId(): String? {
        return _id
    }

    fun genBotName(): String? {
        return _name
    }
}