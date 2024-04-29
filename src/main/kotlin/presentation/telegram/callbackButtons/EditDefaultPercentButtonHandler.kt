package presentation.telegram.callbackButtons

import domain.user.model.User
import domain.user.useCase.EditDefaultPercentUseCase
import presentation.telegram.screens.BotScreen
import presentation.telegram.screens.ErrorScreen
import presentation.telegram.screens.SettingsDefaultPercent

class EditDefaultPercentButtonHandler(
    private val editDefaultPercent: EditDefaultPercentUseCase
) : CallbackButtonHandler {
    override suspend fun execute(
        user: User,
        messageId: Long,
        messageText: String,
        arguments: List<String>
    ): BotScreen {
        val change = arguments
            .firstOrNull()
            ?.toDoubleOrNull()
            ?: return ErrorScreen(user.id, BROKEN_BUTTON)

        return when (val res = editDefaultPercent(user.id, change)) {
            EditDefaultPercentUseCase.Result.Error -> ErrorScreen(user.id, UNKNOWN_BUTTON_ERROR)
            is EditDefaultPercentUseCase.Result.Success -> SettingsDefaultPercent(
                userId = user.id,
                percent = res.newPercent,
                messageId = messageId
            )
        }
    }
}