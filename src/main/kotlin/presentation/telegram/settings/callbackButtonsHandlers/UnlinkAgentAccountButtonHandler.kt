package presentation.telegram.settings.callbackButtonsHandlers

import domain.agent.useCase.UnlinkAgentUseCase
import domain.user.model.User
import presentation.telegram.callbackButtons.BROKEN_BUTTON
import presentation.telegram.callbackButtons.CallbackButtonHandler
import presentation.telegram.screens.BotScreen
import presentation.telegram.screens.ErrorScreen
import presentation.telegram.settings.callbackButtons.UnlinkAgentAccountCallbackButton
import presentation.telegram.settings.screens.SettingsAgent

class UnlinkAgentAccountButtonHandler(
    private val unlinkAgent: UnlinkAgentUseCase
) : CallbackButtonHandler {
    override suspend fun execute(user: User, messageId: Long, messageText: String, arguments: List<String>): BotScreen {
        val callbackData = UnlinkAgentAccountCallbackButton.parseCallbackData(arguments)
            ?: return ErrorScreen(user.id, BROKEN_BUTTON)

        return when (val unlinkResult = unlinkAgent(callbackData.userId)) {
            UnlinkAgentUseCase.Result.Error -> ErrorScreen(user.id, BROKEN_BUTTON)
            is UnlinkAgentUseCase.Result.Success -> SettingsAgent(unlinkResult.user, messageId)
        }
    }
}