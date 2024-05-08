package presentation.common

object AgentUtil {
    fun idToUrl(agentId: String): String {
        return "https://agent.mail.ru/profile/$agentId/ru"
    }

    fun nameToAgentNick(name: String): String {
        return "@$name"
    }
}