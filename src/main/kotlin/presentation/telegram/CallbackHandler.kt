package presentation.telegram

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import presentation.telegram.callbackButtons.*
import presentation.telegram.screens.BotScreen

class CallbackHandler(
    subscribeButtonHandler: SubscribeButtonHandler,
    unsubscribeButtonHandler: UnsubscribeButtonHandler,
    securitiesListBackButtonHandler: SecuritiesListBackButtonHandler,
    securitiesListForwardButtonHandler: SecuritiesListForwardButtonHandler,
    editShareButtonHandler: EditShareButtonHandler,
) {
    private val buttonHandlers = mapOf(
        CallbackButton.Subscribe.callbackData to subscribeButtonHandler,
        CallbackButton.Unsubscribe.callbackData to unsubscribeButtonHandler,
        CallbackButton.SecuritiesListBack.callbackData to securitiesListBackButtonHandler,
        CallbackButton.SecuritiesListForward.callbackData to securitiesListForwardButtonHandler,
        CallbackButton.EditShare.callbackData to editShareButtonHandler
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

        val parsedData = callbackData.split(CALLBACK_BUTTON_ARGUMENT_SEPARATOR)
        val callbackCommand = parsedData.first()

        buttonHandlers[callbackCommand]?.let { handler ->
            val screen = handler.execute(userId, messageId, messageText, parsedData.drop(1))
            _outFlow.emit(screen)
        }
    }
}