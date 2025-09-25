package presentation.telegram

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import presentation.telegram.screens.BotScreen

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
    private val buttonHandlers = mapOf(
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
        val user = findUser(userId).data

        if (user == null) {
            _outFlow.emit(ErrorScreen(userId, USER_NOT_FOUND_MESSAGE))
            return
        }

        val parsedData = callbackData.split(CALLBACK_BUTTON_ARGUMENT_SEPARATOR)
        val callbackCommand = parsedData.first()

        buttonHandlers[callbackCommand]?.let { handler ->
            val screen = handler.execute(user, messageId, messageText, parsedData.drop(1))
            _outFlow.emit(screen)
        }
    }
}