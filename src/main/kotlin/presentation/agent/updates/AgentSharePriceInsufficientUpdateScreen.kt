package presentation.agent.updates

import domain.updateService.model.NotifyShare
import presentation.agent.AGENT_MARKDOWN_V2
import presentation.agent.AgentScreen
import presentation.common.mappers.toInsufficientUpdateText

class AgentSharePriceInsufficientUpdateScreen(chatId: String, val share: NotifyShare) : AgentScreen(chatId) {
    override val parseMode = AGENT_MARKDOWN_V2
    override val text = share.toInsufficientUpdateText()
}