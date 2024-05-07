package presentation.telegram

import domain.common.USER_NOT_FOUND_MESSAGE
import domain.user.useCase.FindUserUseCase
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import presentation.telegram.callbackButtons.*
import presentation.telegram.screens.BotScreen
import presentation.telegram.screens.ErrorScreen
import presentation.telegram.settings.callbackButtons.EditDefaultPercentCallbackButton
import presentation.telegram.settings.callbackButtons.LinkAgentAccountCallbackButton
import presentation.telegram.settings.callbackButtonsHandlers.EditDefaultPercentButtonHandler
import presentation.telegram.settings.callbackButtonsHandlers.LinkAgentAccountButtonHandler

class CallbackHandler(
    editShareButtonHandler: EditShareButtonHandler,
    resetNotificationButtonHandler: ResetNotificationButtonHandler,
    securitiesListBackButtonHandler: SecuritiesListBackButtonHandler,
    securitiesListForwardButtonHandler: SecuritiesListForwardButtonHandler,
    sharePercentButtonHandler: SharePercentButtonHandler,
    subscribeButtonHandler: SubscribeButtonHandler,
    unsubscribeButtonHandler: UnsubscribeButtonHandler,
    editDefaultPercentButtonHandler: EditDefaultPercentButtonHandler,
    linkAgentAccountButtonHandler: LinkAgentAccountButtonHandler,
    private val findUser: FindUserUseCase
) {
    private val buttonHandlers = mapOf(
        CallbackButton.EditShare.callbackData to editShareButtonHandler,
        CallbackButton.ResetNotification.callbackData to resetNotificationButtonHandler,
        CallbackButton.SecuritiesListBack.callbackData to securitiesListBackButtonHandler,
        CallbackButton.SecuritiesListForward.callbackData to securitiesListForwardButtonHandler,
        CallbackButton.SharePercent.callbackData to sharePercentButtonHandler,
        CallbackButton.Subscribe.callbackData to subscribeButtonHandler,
        CallbackButton.Unsubscribe.callbackData to unsubscribeButtonHandler,
        EditDefaultPercentCallbackButton.callbackData to editDefaultPercentButtonHandler,
        LinkAgentAccountCallbackButton.callbackData to linkAgentAccountButtonHandler,
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