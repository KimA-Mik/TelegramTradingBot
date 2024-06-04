package presentation.agent

import domain.updateService.UpdateService
import domain.updateService.updates.agentUpdates.AgentIndicatorUpdate
import domain.updateService.updates.agentUpdates.AgentSharePriceInsufficientUpdate
import domain.updateService.updates.agentUpdates.AgentShareUpdate
import domain.updateService.updates.agentUpdates.AgentUpdate
import kotlinx.coroutines.flow.mapNotNull
import presentation.agent.updates.AgentSharePriceInsufficientUpdateScreen
import presentation.agent.updates.AgentShareUpdateScreen

class AgentUpdateHandler(
    updateService: UpdateService
) {
    val updateScreens = updateService.agentUpdates.mapNotNull {
        it.toScreen()
    }

    private fun AgentUpdate.toScreen(): AgentScreen? {
        return when (this) {
            is AgentSharePriceInsufficientUpdate -> AgentSharePriceInsufficientUpdateScreen(
                chatId = chatId,
                share = share
            )

            is AgentShareUpdate -> AgentShareUpdateScreen(
                chatId = chatId,
                share = share
            )

            is AgentIndicatorUpdate -> null
        }
    }
}