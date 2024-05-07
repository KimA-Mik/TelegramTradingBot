package presentation.telegram.settings.screens

import com.github.kotlintelegrambot.entities.KeyboardReplyMarkup
import com.github.kotlintelegrambot.entities.keyboard.KeyboardButton
import domain.user.model.User
import presentation.telegram.BotTextCommands
import presentation.telegram.screens.BotScreen

class SettingsAgent(user: User) : BotScreen(user.id) {
    override val text = "Настройки Agent"
    override val replyMarkup = KeyboardReplyMarkup(
        listOf(
            listOf(KeyboardButton(BotTextCommands.Root.text))
        ),
        resizeKeyboard = true
    )
    override val parseMode = null
}