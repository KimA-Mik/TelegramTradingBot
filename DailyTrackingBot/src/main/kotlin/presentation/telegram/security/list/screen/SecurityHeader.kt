package presentation.telegram.security.list.screen

import presentation.telegram.core.screen.BotScreen

class SecurityHeader(id: Long) : BotScreen(id) {
    override val text = "Ваши бумаги:"
    override val replyMarkup = rootReplayMarkup
}