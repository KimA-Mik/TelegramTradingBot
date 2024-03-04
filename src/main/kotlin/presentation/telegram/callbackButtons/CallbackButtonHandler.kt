package presentation.telegram.callbackButtons

import presentation.telegram.screens.BotScreen

interface CallbackButtonHandler {
    suspend fun execute(userId: Long, messageId: Long, messageText: String): BotScreen
}