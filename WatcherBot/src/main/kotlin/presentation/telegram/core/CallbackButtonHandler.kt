package presentation.telegram.core

import domain.user.model.User
import kotlinx.coroutines.flow.Flow
import presentation.telegram.core.screen.BotScreen


interface CallbackButtonHandler {
    suspend fun execute(user: User, messageId: Long, messageText: String, arguments: List<String>): Flow<BotScreen>

    companion object {
        const val CALLBACK_BUTTON_ARGUMENT_SEPARATOR = '&'

        fun parseCallbackData(string: String): ParseResult {
            val data = string.split(CALLBACK_BUTTON_ARGUMENT_SEPARATOR)
            return ParseResult(data.first(), data.drop(1))
        }

        data class ParseResult(
            val command: String,
            val arguments: List<String>
        )
    }
}