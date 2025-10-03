package presentation.telegram.security.screen

import presentation.telegram.core.screen.BotScreen

class EditPriceScreen(userId: Long) : BotScreen(userId) {
    override val text = "Введите ожидаемую цену, при приближении к которой вы будете получать уведомление."
    override val replyMarkup = basicReplayMarkup
}