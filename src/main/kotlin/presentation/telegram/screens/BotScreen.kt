package presentation.telegram.screens

import com.github.kotlintelegrambot.entities.ParseMode
import com.github.kotlintelegrambot.entities.ReplyMarkup

abstract class BotScreen(val id: Long, val messageId: Long? = null) {
    abstract val text: String
    abstract val replyMarkup: ReplyMarkup?
    abstract val parseMode: ParseMode?
    open val disableWebPagePreview = false
}



