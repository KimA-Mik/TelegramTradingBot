package presentation.agent.registration

import presentation.agent.AgentScreen

class UserAlreadyLinkedScreen(chatId: String) : AgentScreen(chatId) {
    override val text = "Аккаунт уже привязан"
}