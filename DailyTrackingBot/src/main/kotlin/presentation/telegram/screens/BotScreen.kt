package presentation.telegram.screens

import com.github.kotlintelegrambot.entities.ParseMode
import com.github.kotlintelegrambot.entities.ReplyMarkup

abstract class BotScreen(val id: Long, val messageId: Long? = null) {
    abstract val text: String
    open val replyMarkup: ReplyMarkup? = null
    open val parseMode: ParseMode? = null
    open val disableWebPagePreview = false
}