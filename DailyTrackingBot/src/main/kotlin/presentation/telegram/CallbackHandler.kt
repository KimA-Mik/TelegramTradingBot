package presentation.telegram

import domain.user.usecase.FindUserUseCase
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.emitAll
import presentation.telegram.core.CallbackButtonHandler
import presentation.telegram.core.UiError
import presentation.telegram.core.screen.BotScreen
import presentation.telegram.core.screen.ErrorScreen
import presentation.telegram.security.callbackbutton.*
import presentation.telegram.security.search.callbackbutton.*

class CallbackHandler(
    /* Ticker search screen */
    tickerSuggestionCallbackHandler: TickerSuggestionCallbackHandler,
    subscribeToSecurityCallbackHandler: SubscribeToSecurityCallbackHandler,
    unsubscribeFromSecurityCallbackHandler: UnsubscribeFromSecurityCallbackHandler,
    /* Security screen */
    toggleIsActiveCallbackHandler: ToggleIsActiveCallbackHandler,
    toggleRemainActiveCallbackHandler: ToggleRemainActiveCallbackHandler,
    toggleShowNoteCallbackHandler: ToggleShowNoteCallbackHandler,
    private val findUser: FindUserUseCase
) {
    private val buttonHandlers = mapOf(
        /* Ticker search screen */
        TickerSuggestionCallbackButton.callbackName to tickerSuggestionCallbackHandler,
        SubscribeToSecurityCallbackButton.callbackName to subscribeToSecurityCallbackHandler,
        UnsubscribeFromSecurityCallbackButton.callbackName to unsubscribeFromSecurityCallbackHandler,
        /* Security screen */
        ToggleIsActiveCallbackButton.callbackName to toggleIsActiveCallbackHandler,
        ToggleRemainActiveCallbackButton.callbackName to toggleRemainActiveCallbackHandler,
        ToggleShowNoteCallbackButton.callbackName to toggleShowNoteCallbackHandler
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