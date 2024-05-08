package presentation.telegram.settings.callbackButtonsHandlers

import domain.agent.useCase.SetAgentNotificationsStatusUseCase
import domain.user.model.User
import presentation.telegram.callbackButtons.CallbackButtonHandler
import presentation.telegram.screens.BotScreen
import presentation.telegram.screens.ErrorScreen
import presentation.telegram.settings.NO_LINKED_AGENT
import presentation.telegram.settings.UNABLE_TO_DISABLE_AGENT_NOTIFICATIONS
import presentation.telegram.settings.screens.SettingsAgent

class DisableAgentNotificationsButtonHandler(
    private val setAgentNotificationsStatus: SetAgentNotificationsStatusUseCase
) : CallbackButtonHandler {
    override suspend fun execute(user: User, messageId: Long, messageText: String, arguments: List<String>): BotScreen {
        return when (val res = setAgentNotificationsStatus(user.id, false)) {
            SetAgentNotificationsStatusUseCase.Result.Error -> ErrorScreen(
                user.id,
                UNABLE_TO_DISABLE_AGENT_NOTIFICATIONS
            )

            is SetAgentNotificationsStatusUseCase.Result.Success -> SettingsAgent(res.user, messageId)
            SetAgentNotificationsStatusUseCase.Result.NoAgent -> ErrorScreen(user.id, NO_LINKED_AGENT)
        }
    }
}