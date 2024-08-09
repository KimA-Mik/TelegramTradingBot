package presentation.telegram

import domain.common.USER_NOT_FOUND_MESSAGE
import domain.user.useCase.FindUserUseCase
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import presentation.telegram.callbackButtons.*
import presentation.telegram.screens.BotScreen
import presentation.telegram.screens.ErrorScreen
import presentation.telegram.settings.callbackButtons.*
import presentation.telegram.settings.callbackButtons.indicators.bollingerBands.ResetBbDefaultCallbackButton
import presentation.telegram.settings.callbackButtons.indicators.bollingerBands.SwitchBbDefaultCallbackButton
import presentation.telegram.settings.callbackButtons.indicators.rsi.ResetRsiDefaultCallbackButton
import presentation.telegram.settings.callbackButtons.indicators.rsi.SwitchRsiDefaultCallbackButton
import presentation.telegram.settings.callbackButtonsHandlers.*
import presentation.telegram.settings.callbackButtonsHandlers.indicators.bollingerBands.ResetBbDefaultButtonHandler
import presentation.telegram.settings.callbackButtonsHandlers.indicators.bollingerBands.SwitchBbDefaultButtonHandler
import presentation.telegram.settings.callbackButtonsHandlers.indicators.rsi.ResetRsiDefaultButtonHandler
import presentation.telegram.settings.callbackButtonsHandlers.indicators.rsi.SwitchRsiDefaultButtonHandler

class CallbackHandler(
    editShareButtonHandler: EditShareButtonHandler,
    resetNotificationButtonHandler: ResetNotificationButtonHandler,
    securitiesListBackButtonHandler: SecuritiesListBackButtonHandler,
    securitiesListForwardButtonHandler: SecuritiesListForwardButtonHandler,
    sharePercentButtonHandler: SharePercentButtonHandler,
    subscribeButtonHandler: SubscribeButtonHandler,
    unsubscribeButtonHandler: UnsubscribeButtonHandler,

    editDefaultPercentButtonHandler: EditDefaultPercentButtonHandler,
    disableAgentNotificationsButtonHandler: DisableAgentNotificationsButtonHandler,
    enableAgentNotificationsButtonHandler: EnableAgentNotificationsButtonHandler,
    linkAgentAccountButtonHandler: LinkAgentAccountButtonHandler,
    unlinkAgentAccountButtonHandler: UnlinkAgentAccountButtonHandler,

    resetBbDefaultButtonHandler: ResetBbDefaultButtonHandler,
    switchBbDefaultButtonHandler: SwitchBbDefaultButtonHandler,
    resetRsiDefaultButtonHandler: ResetRsiDefaultButtonHandler,
    switchRsiDefaultButtonHandler: SwitchRsiDefaultButtonHandler,
    private val findUser: FindUserUseCase
) {
    private val buttonHandlers = mapOf(
        /* Ungrouped */
        CallbackButton.EditShare.callbackData to editShareButtonHandler,
        CallbackButton.ResetNotification.callbackData to resetNotificationButtonHandler,
        CallbackButton.SecuritiesListBack.callbackData to securitiesListBackButtonHandler,
        CallbackButton.SecuritiesListForward.callbackData to securitiesListForwardButtonHandler,
        CallbackButton.SharePercent.callbackData to sharePercentButtonHandler,
        CallbackButton.Subscribe.callbackData to subscribeButtonHandler,
        CallbackButton.Unsubscribe.callbackData to unsubscribeButtonHandler,

        /* Settings */
        EditDefaultPercentCallbackButton.callbackData to editDefaultPercentButtonHandler,
        DisableAgentNotificationsCallbackButton.callbackData to disableAgentNotificationsButtonHandler,
        EnableAgentNotificationsCallbackButton.callbackData to enableAgentNotificationsButtonHandler,
        LinkAgentAccountCallbackButton.callbackData to linkAgentAccountButtonHandler,
        UnlinkAgentAccountCallbackButton.callbackData to unlinkAgentAccountButtonHandler,
        /* Indicator settings */
        ResetBbDefaultCallbackButton.callbackData to resetBbDefaultButtonHandler,
        SwitchBbDefaultCallbackButton.callbackData to switchBbDefaultButtonHandler,
        ResetRsiDefaultCallbackButton.callbackData to resetRsiDefaultButtonHandler,
        SwitchRsiDefaultCallbackButton.callbackData to switchRsiDefaultButtonHandler,
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