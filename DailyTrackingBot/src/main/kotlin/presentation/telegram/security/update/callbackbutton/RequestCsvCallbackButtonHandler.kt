package presentation.telegram.security.update.callbackbutton

import domain.user.model.User
import kotlinx.coroutines.flow.flow
import presentation.telegram.core.CallbackButtonHandler
import presentation.telegram.core.screen.BotScreen

class RequestCsvCallbackButtonHandler : CallbackButtonHandler {
    override suspend fun execute(
        user: User,
        messageId: Long,
        messageText: String,
        arguments: List<String>
    ) = flow<BotScreen> {

    }
}