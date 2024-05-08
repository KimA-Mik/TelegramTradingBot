package presentation.agent

abstract class AgentScreen(
    val chatId: String,
) {
    abstract val text: String
}