package domain.agent.repositoty

interface AgentRepository {
    fun setBotId(id: String)
    fun setBotName(name: String)
    fun getBotId(): String?
    fun genBotName(): String?
}