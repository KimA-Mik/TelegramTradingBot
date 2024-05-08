package presentation.agent

import domain.agent.useCase.LinkAgentAccountUseCase
import presentation.agent.registration.LinkingErrorScreen
import presentation.agent.registration.NoLinkRequestScreen
import presentation.agent.registration.SuccessfulLinkageScreen
import presentation.agent.registration.UserAlreadyLinkedScreen

class AgentEventHandler(
    private val linkAgentAccount: LinkAgentAccountUseCase
) {
    suspend fun handleEvent(event: AgentBotEvent): AgentScreen {
        return when (event) {
            is AgentBotEvent.NewMessageEvent -> handleNewMessageEvent(event)
        }
    }

    private suspend fun handleNewMessageEvent(event: AgentBotEvent.NewMessageEvent): AgentScreen {
        return when (linkAgentAccount(event.chatId, event.text)) {
            LinkAgentAccountUseCase.Result.AlreadyLinked -> UserAlreadyLinkedScreen(event.chatId)
            LinkAgentAccountUseCase.Result.Error -> LinkingErrorScreen(event.chatId)
            LinkAgentAccountUseCase.Result.NoRequest -> NoLinkRequestScreen(event.chatId)
            is LinkAgentAccountUseCase.Result.Success -> SuccessfulLinkageScreen(event.chatId)
        }
    }
}