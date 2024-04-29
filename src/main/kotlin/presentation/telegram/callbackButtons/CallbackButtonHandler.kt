package presentation.telegram.callbackButtons

import domain.user.model.User
import presentation.telegram.screens.BotScreen

interface CallbackButtonHandler {
    suspend fun execute(user: User, messageId: Long, messageText: String, arguments: List<String>): BotScreen
}