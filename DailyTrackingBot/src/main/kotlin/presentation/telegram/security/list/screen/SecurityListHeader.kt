package presentation.telegram.security.list.screen

import com.github.kotlintelegrambot.entities.ParseMode
import presentation.telegram.core.screen.BotScreen

class SecurityListHeader(id: Long) : BotScreen(id) {
    override val text = "*Отслеживаемые бумаги:*"
    override val replyMarkup = basicReplayMarkup
    override val parseMode = ParseMode.MARKDOWN
}