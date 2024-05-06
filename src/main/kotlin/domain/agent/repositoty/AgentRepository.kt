package domain.agent.repositoty

interface AgentRepository {
    fun setBotId(agentId: String)
    fun getBotId(): String?
}