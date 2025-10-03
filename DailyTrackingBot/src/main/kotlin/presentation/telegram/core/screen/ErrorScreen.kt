package presentation.telegram.core.screen

import presentation.telegram.core.UiError

class ErrorScreen(userId: Long, error: UiError) : BotScreen(userId) {
    override val text = error.render()
    override val replyMarkup = error.replyMarkup
    override val parseMode = error.parseMode
}