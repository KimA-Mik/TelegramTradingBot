package presentation.telegram.screens

import com.github.kotlintelegrambot.entities.ReplyMarkup

class Greeting(id: Long) : BotScreen(id) {
    override val text = "Добро пожаловать в торговый бот."
    override val parseMode = null

    override val replyMarkup: ReplyMarkup
        get() = TODO("Not yet implemented")
}
