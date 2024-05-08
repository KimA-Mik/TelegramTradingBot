package presentation.agent

abstract class AgentScreen(
    val chatId: String,
    open val parseMode: String? = null
) {
    abstract val text: String
}