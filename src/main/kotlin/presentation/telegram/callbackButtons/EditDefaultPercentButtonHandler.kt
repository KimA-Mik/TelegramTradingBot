package presentation.telegram.callbackButtons

import domain.user.useCase.EditDefaultPercentUseCase
import presentation.telegram.screens.BotScreen
import presentation.telegram.screens.ErrorScreen
import presentation.telegram.screens.SettingsDefaultPercent

class EditDefaultPercentButtonHandler(
    private val editDefaultPercent: EditDefaultPercentUseCase
) : CallbackButtonHandler {
    override suspend fun execute(
        userId: Long,
        messageId: Long,
        messageText: String,
        arguments: List<String>
    ): BotScreen {
        val change = arguments
            .firstOrNull()
            ?.toDoubleOrNull()
            ?: return ErrorScreen(userId, BROKEN_BUTTON)

        return when (val res = editDefaultPercent(userId, change)) {
            EditDefaultPercentUseCase.Result.Error -> ErrorScreen(userId, UNKNOWN_BUTTON_ERROR)
            is EditDefaultPercentUseCase.Result.Success -> SettingsDefaultPercent(
                userId = userId,
                percent = res.newPercent,
                messageId = messageId
            )
        }
    }
}