package presentation.telegram.core.screen

import com.github.kotlintelegrambot.entities.KeyboardReplyMarkup
import com.github.kotlintelegrambot.entities.ParseMode
import com.github.kotlintelegrambot.entities.ReplyMarkup
import com.github.kotlintelegrambot.entities.keyboard.KeyboardButton
import presentation.telegram.core.DefaultCommands

abstract class BotScreen(val id: Long, val messageId: Long? = null) {
    abstract val text: String
    open val replyMarkup: ReplyMarkup? = null
    open val parseMode: ParseMode? = null
    open val disableWebPagePreview = false

    companion object {
        val basicReplayMarkup = KeyboardReplyMarkup(
            listOf(
                listOf(KeyboardButton(DefaultCommands.Pop.text))
            ),
            resizeKeyboard = true
        )
    }
}