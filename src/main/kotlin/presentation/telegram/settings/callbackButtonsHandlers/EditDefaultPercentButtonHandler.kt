package presentation.telegram.settings.callbackButtonsHandlers

import domain.user.model.User
import domain.user.useCase.EditDefaultPercentUseCase
import presentation.telegram.callbackButtons.BROKEN_BUTTON
import presentation.telegram.callbackButtons.CallbackButtonHandler
import presentation.telegram.callbackButtons.UNKNOWN_BUTTON_ERROR
import presentation.telegram.screens.BotScreen
import presentation.telegram.screens.ErrorScreen
import presentation.telegram.settings.callbackButtons.EditDefaultPercentCallbackButton
import presentation.telegram.settings.screens.SettingsDefaultPercent

class EditDefaultPercentButtonHandler(
    private val editDefaultPercent: EditDefaultPercentUseCase
) : CallbackButtonHandler {
    override suspend fun execute(
        user: User,
        messageId: Long,
        messageText: String,
        arguments: List<String>
    ): BotScreen {
        val callbackData = EditDefaultPercentCallbackButton
            .parseCallbackData(arguments)
            ?: return ErrorScreen(user.id, BROKEN_BUTTON)

        return when (val res = editDefaultPercent(user.id, callbackData.percent)) {
            EditDefaultPercentUseCase.Result.Error -> ErrorScreen(user.id, UNKNOWN_BUTTON_ERROR)
            is EditDefaultPercentUseCase.Result.Success -> SettingsDefaultPercent(
                userId = user.id,
                percent = res.newPercent,
                messageId = messageId
            )
        }
    }
}