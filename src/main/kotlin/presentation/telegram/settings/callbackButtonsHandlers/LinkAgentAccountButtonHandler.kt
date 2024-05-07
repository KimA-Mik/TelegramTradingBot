package presentation.telegram.settings.callbackButtonsHandlers

import domain.agent.useCase.CreateAgentLinkRequestUseCase
import domain.agent.useCase.GetAgentBotIdUseCase
import domain.user.model.User
import presentation.telegram.callbackButtons.BROKEN_BUTTON
import presentation.telegram.callbackButtons.CallbackButtonHandler
import presentation.telegram.screens.BotScreen
import presentation.telegram.screens.ErrorScreen
import presentation.telegram.settings.NO_AGENT_BOT
import presentation.telegram.settings.UNABLE_TO_CREATE_AGENT_LINK_REQUEST
import presentation.telegram.settings.callbackButtons.LinkAgentAccountCallbackButton
import presentation.telegram.settings.screens.AgentLinkRequestScreen

class LinkAgentAccountButtonHandler(
    private val getAgentBotId: GetAgentBotIdUseCase,
    private val createAgentLinkRequest: CreateAgentLinkRequestUseCase
) : CallbackButtonHandler {
    override suspend fun execute(
        user: User,
        messageId: Long,
        messageText: String,
        arguments: List<String>
    ): BotScreen {
        val callbackData = LinkAgentAccountCallbackButton.parseCallbackData(arguments)
            ?: return ErrorScreen(messageId, BROKEN_BUTTON)

        val agentBotInfo = getAgentBotId()
            ?: return ErrorScreen(messageId, NO_AGENT_BOT)

        val result = createAgentLinkRequest(callbackData.userId)

        if (result !is CreateAgentLinkRequestUseCase.Result.Success) {
            return ErrorScreen(messageId, UNABLE_TO_CREATE_AGENT_LINK_REQUEST)
        }

        return AgentLinkRequestScreen(
            userId = user.id,
            userCode = result.code,
            agentBotInfo = agentBotInfo
        )
    }
}