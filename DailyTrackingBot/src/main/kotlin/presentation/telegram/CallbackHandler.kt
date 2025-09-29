package presentation.telegram

import domain.user.usecase.FindUserUseCase
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.emitAll
import presentation.telegram.core.CallbackButtonHandler
import presentation.telegram.core.UiError
import presentation.telegram.core.screen.BotScreen
import presentation.telegram.core.screen.ErrorScreen

class CallbackHandler(
//    /* Ungrouped */
//    resetNotificationButtonHandler: ResetNotificationButtonHandler,
//    subscribeButtonHandler: SubscribeButtonHandler,
//    unsubscribeButtonHandler: UnsubscribeButtonHandler,
//
//    /* Securities List */
//    editShareButtonHandler: EditShareButtonHandler,
//    securitiesListBackButtonHandler: SecuritiesListBackButtonHandler,
//    securitiesListForwardButtonHandler: SecuritiesListForwardButtonHandler,
//    sharePercentButtonHandler: SharePercentButtonHandler,
//    switchShareIndicatorCallbackButtonHandler: SwitchShareIndicatorCallbackButtonHandler,
//
//    /* Settings */
//    editDefaultPercentButtonHandler: EditDefaultPercentButtonHandler,
//    /* Indicator settings */
//    resetBbDefaultButtonHandler: ResetBbDefaultButtonHandler,
//    switchBbDefaultButtonHandler: SwitchBbDefaultButtonHandler,
//    resetRsiDefaultButtonHandler: ResetRsiDefaultButtonHandler,
//    switchRsiDefaultButtonHandler: SwitchRsiDefaultButtonHandler,
    private val findUser: FindUserUseCase
) {
    private val buttonHandlers = mapOf<String, CallbackButtonHandler>(
//        /* Ungrouped */
//        CallbackButton.ResetNotification.callbackData to resetNotificationButtonHandler,
//        CallbackButton.Subscribe.callbackData to subscribeButtonHandler,
//        CallbackButton.Unsubscribe.callbackData to unsubscribeButtonHandler,
//
//        /* Securities List */
//        EditShare.callbackData to editShareButtonHandler,
//        SecuritiesListBack.callbackData to securitiesListBackButtonHandler,
//        SecuritiesListForward.callbackData to securitiesListForwardButtonHandler,
//        SharePercent.callbackData to sharePercentButtonHandler,
//        SwitchShareIndicatorCallbackButton.callbackData to switchShareIndicatorCallbackButtonHandler,
//
//        /* Settings */
//        EditDefaultPercentCallbackButton.callbackData to editDefaultPercentButtonHandler,
//        /* Indicator settings */
//        ResetBbDefaultCallbackButton.callbackData to resetBbDefaultButtonHandler,
//        SwitchBbDefaultCallbackButton.callbackData to switchBbDefaultButtonHandler,
//        ResetRsiDefaultCallbackButton.callbackData to resetRsiDefaultButtonHandler,
//        SwitchRsiDefaultCallbackButton.callbackData to switchRsiDefaultButtonHandler,
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