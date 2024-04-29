package presentation.telegram.screens

import com.github.kotlintelegrambot.entities.KeyboardReplyMarkup
import com.github.kotlintelegrambot.entities.ParseMode
import com.github.kotlintelegrambot.entities.keyboard.KeyboardButton
import presentation.telegram.BotTextCommands

class Root(id: Long) : BotScreen(id) {
    override val text =
        "*Супер бот:*\n" +
                "• ${BotTextCommands.MySecurities.text}\n" +
                "• ${BotTextCommands.SearchSecurities.text}\n" +
                "• ${BotTextCommands.Settings.text}"
    override val replyMarkup = _replayMarkup
    override val parseMode = ParseMode.MARKDOWN_V2

    companion object {
        private val _replayMarkup = KeyboardReplyMarkup(
            listOf(
                listOf(
                    KeyboardButton(BotTextCommands.SearchSecurities.text),
                    KeyboardButton(BotTextCommands.MySecurities.text),
                    KeyboardButton(BotTextCommands.Settings.text)
                ),
            ),
            resizeKeyboard = true
        )
    }
}