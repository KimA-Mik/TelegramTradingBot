package presentation.agent

sealed interface AgentBotEvent {
    data class NewMessageEvent(val chatId: String, val text: String) : AgentBotEvent
}