package presentation.telegram

import domain.user.usecase.FindUserUseCase
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.emitAll
import presentation.telegram.core.CallbackButtonHandler
import presentation.telegram.core.UiError
import presentation.telegram.core.screen.BotScreen
import presentation.telegram.core.screen.ErrorScreen
import presentation.telegram.security.edit.callbackbutton.*
import presentation.telegram.security.list.callbackbutton.*
import presentation.telegram.security.search.callbackbutton.*
import presentation.telegram.settings.root.callbackbutton.ToggleSrsiAlertCallbackButton
import presentation.telegram.settings.root.callbackbutton.ToggleSrsiAlertCallbackButtonHandler

class CallbackHandler(
    /* Ticker search screen */
    tickerSuggestionCallbackHandler: TickerSuggestionCallbackHandler,
    subscribeToSecurityCallbackHandler: SubscribeToSecurityCallbackHandler,
    unsubscribeFromSecurityCallbackHandler: UnsubscribeFromSecurityCallbackHandler,
    /* Security screen */
    changeDefaultPriceProlongationCallbackHandler: ChangeDefaultPriceProlongationCallbackHandler,
    resetPriceCallbackHandler: ResetPriceCallbackHandler,
    toggleIsActiveCallbackHandler: ToggleIsActiveCallbackHandler,
    toggleRemainActiveCallbackHandler: ToggleRemainActiveCallbackHandler,
    toggleShowNoteCallbackHandler: ToggleShowNoteCallbackHandler,
    /* Security list screen */
    editSecurityCallbackButtonHandler: EditSecurityCallbackButtonHandler,
    securitiesListBackwardCallbackButtonHandler: SecuritiesListBackwardCallbackButtonHandler,
    securitiesListForwardCallbackButtonHandler: SecuritiesListForwardCallbackButtonHandler,
    /* Settings */
    toggleSrsiAlertCallbackButtonHandler: ToggleSrsiAlertCallbackButtonHandler,
    private val findUser: FindUserUseCase
) {
    private val buttonHandlers = mapOf(
        /* Ticker search screen */
        TickerSuggestionCallbackButton.callbackName to tickerSuggestionCallbackHandler,
        SubscribeToSecurityCallbackButton.callbackName to subscribeToSecurityCallbackHandler,
        UnsubscribeFromSecurityCallbackButton.callbackName to unsubscribeFromSecurityCallbackHandler,
        /* Security screen */
        ChangeDefaultPriceProlongationCallbackButton.callbackName to changeDefaultPriceProlongationCallbackHandler,
        ResetPriceCallbackButton.callbackName to resetPriceCallbackHandler,
        ToggleIsActiveCallbackButton.callbackName to toggleIsActiveCallbackHandler,
        ToggleRemainActiveCallbackButton.callbackName to toggleRemainActiveCallbackHandler,
        ToggleShowNoteCallbackButton.callbackName to toggleShowNoteCallbackHandler,
        /* Security list screen */
        EditSecurityCallbackButton.callbackName to editSecurityCallbackButtonHandler,
        SecuritiesListBackwardCallbackButton.callbackName to securitiesListBackwardCallbackButtonHandler,
        SecuritiesListForwardCallbackButton.callbackName to securitiesListForwardCallbackButtonHandler,
        /* Settings */
        ToggleSrsiAlertCallbackButton.callbackName to toggleSrsiAlertCallbackButtonHandler
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