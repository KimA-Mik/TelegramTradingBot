package presentation.agent

import domain.updateService.UpdateService
import domain.updateService.agentUpdates.AgentSharePriceInsufficientUpdate
import domain.updateService.agentUpdates.AgentShareUpdate
import domain.updateService.agentUpdates.AgentUpdate
import kotlinx.coroutines.flow.map
import presentation.agent.updates.AgentSharePriceInsufficientUpdateScreen
import presentation.agent.updates.AgentShareUpdateScreen

class AgentUpdateHandler(
    updateService: UpdateService
) {
    val updateScreens = updateService.agentUpdates.map {
        it.toScreen()
    }

    private fun AgentUpdate.toScreen(): AgentScreen {
        return when (this) {
            is AgentSharePriceInsufficientUpdate -> AgentSharePriceInsufficientUpdateScreen(
                chatId = chatId,
                share = share
            )

            is AgentShareUpdate -> AgentShareUpdateScreen(
                chatId = chatId,
                share = share
            )
        }
    }
}