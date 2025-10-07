package presentation.telegram.core.screen

import com.github.kotlintelegrambot.entities.KeyboardReplyMarkup
import com.github.kotlintelegrambot.entities.ParseMode
import com.github.kotlintelegrambot.entities.keyboard.KeyboardButton

class Root(userId: Long, messageId: Long? = null) : BotScreen(userId, messageId) {
    override val text = buildString {
        appendLine("*Бот:*")
        Commands.entries.forEach { appendLine("• ${it.text}") }
    }
    override val replyMarkup = _replayMarkup
    override val parseMode = ParseMode.MARKDOWN_V2

    companion object {
        private val _replayMarkup = KeyboardReplyMarkup(
            listOf(
                buildList {
                    Commands.entries.forEach { add(KeyboardButton(it.text)) }
                }
            ),
            resizeKeyboard = true
        )
    }

    enum class Commands(val text: String) {
        Search("Поиск"),
        Security("Бумага")
    }
}