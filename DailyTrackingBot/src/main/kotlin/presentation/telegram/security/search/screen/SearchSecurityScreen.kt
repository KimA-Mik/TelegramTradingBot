package presentation.telegram.security.search.screen

import com.github.kotlintelegrambot.entities.KeyboardReplyMarkup
import com.github.kotlintelegrambot.entities.keyboard.KeyboardButton
import presentation.telegram.core.DefaultCommands
import presentation.telegram.core.screen.BotScreen
import presentation.telegram.security.search.textmodel.SearchSecurityTextModel

class SearchSecurityScreen(
    userId: Long,
    private val showFillSuggestions: Boolean
) : BotScreen(userId) {
    override val text = "Введите тикер бумаги"
    override val replyMarkup = calculateReplyMarkup()

    private fun calculateReplyMarkup(): KeyboardReplyMarkup {
        val keys = buildList {
            if (showFillSuggestions) {
                add(listOf(KeyboardButton(SearchSecurityTextModel.Commands.UseDefaultSecurities.text)))
            }
            add(listOf(KeyboardButton(DefaultCommands.Root.text)))
        }
        return KeyboardReplyMarkup(
            keys,
            resizeKeyboard = true
        )
    }
}