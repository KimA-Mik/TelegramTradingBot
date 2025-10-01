package presentation.telegram

import domain.user.usecase.FindUserUseCase
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.emitAll
import presentation.telegram.core.CallbackButtonHandler
import presentation.telegram.core.UiError
import presentation.telegram.core.screen.BotScreen
import presentation.telegram.core.screen.ErrorScreen
import presentation.telegram.security.callbackbutton.TickerSuggestionCallbackButton
import presentation.telegram.security.callbackbutton.TickerSuggestionCallbackHandler

class CallbackHandler(
    /* Security screen */
    tickerSuggestionCallbackHandler: TickerSuggestionCallbackHandler,
    private val findUser: FindUserUseCase
) {
    private val buttonHandlers = mapOf<String, CallbackButtonHandler>(
        TickerSuggestionCallbackButton.callbackData to tickerSuggestionCallbackHandler
    )

    private val _outFlow = MutableSharedFlow<BotScreen>()
    val outFlow = _outFlow.asSharedFlow()

    suspend fun handleCallback(
        callbackData: String,
        userId: Long,
        messageId: Long,
        messageText: String
    ) {
        if (userId == 0L || messageId == 0L) return
        val user = findUser(userId).getOrElse {
            _outFlow.emit(ErrorScreen(userId, UiError.UnregisteredUserError))
            return
        }

        val parseResult = CallbackButtonHandler.parseCallbackData(callbackData)
        buttonHandlers[parseResult.command]?.let { handler ->
            _outFlow.emitAll(handler.execute(user, messageId, messageText, parseResult.arguments))
        }
    }
}