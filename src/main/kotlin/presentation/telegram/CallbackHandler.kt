package presentation.telegram

import domain.common.USER_NOT_FOUND_MESSAGE
import domain.user.useCase.FindUserUseCase
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import presentation.telegram.callbackButtons.*
import presentation.telegram.screens.BotScreen
import presentation.telegram.screens.ErrorScreen
import presentation.telegram.securitiesList.callbackButtons.EditShare
import presentation.telegram.securitiesList.callbackButtons.SecuritiesListBack
import presentation.telegram.securitiesList.callbackButtons.SecuritiesListForward
import presentation.telegram.securitiesList.callbackButtons.SharePercent
import presentation.telegram.securitiesList.callbackButtonsHandlers.EditShareButtonHandler
import presentation.telegram.securitiesList.callbackButtonsHandlers.SecuritiesListBackButtonHandler
import presentation.telegram.securitiesList.callbackButtonsHandlers.SecuritiesListForwardButtonHandler
import presentation.telegram.securitiesList.callbackButtonsHandlers.SharePercentButtonHandler
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
        CallbackButton.ResetNotification.callbackData to resetNotificationButtonHandler,
        CallbackButton.Subscribe.callbackData to subscribeButtonHandler,
        CallbackButton.Unsubscribe.callbackData to unsubscribeButtonHandler,

        /* Securities List */
        EditShare.callbackData to editShareButtonHandler,
        SecuritiesListBack.callbackData to securitiesListBackButtonHandler,
        SecuritiesListForward.callbackData to securitiesListForwardButtonHandler,
        SharePercent.callbackData to sharePercentButtonHandler,

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