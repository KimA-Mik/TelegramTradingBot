package presentation.agent.registration

import presentation.agent.AgentScreen

class SuccessfulLinkageScreen(chatId: String) : AgentScreen(chatId) {
    override val text = "Аккаунт успешно привязан"
}