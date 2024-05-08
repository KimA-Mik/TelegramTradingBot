package presentation.agent.registration

import presentation.agent.AgentScreen

class LinkingErrorScreen(chatId: String) : AgentScreen(chatId) {
    override val text = "При связи аккаунтов произошла ошибка"
}