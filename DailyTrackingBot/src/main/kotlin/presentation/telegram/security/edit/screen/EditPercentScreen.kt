package presentation.telegram.security.edit.screen

import presentation.telegram.core.screen.BotScreen

class EditPercentScreen(userId: Long) : BotScreen(userId) {
    override val text =
        "Введите максимальный процент расхождения цены от желаемой, при котором вы будете получать уведомление."
    override val replyMarkup = basicReplayMarkup
}