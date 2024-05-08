package presentation.agent.registration

import presentation.agent.AgentScreen

class NoLinkRequestScreen(chatId: String) : AgentScreen(chatId) {
    override val text = "Отсутствует запрос на привязку"
}