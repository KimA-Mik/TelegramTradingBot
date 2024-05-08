package domain.updateService.agentUpdates

import domain.updateService.model.NotifyShare

class AgentSharePriceInsufficientUpdate(
    chatId: String,
    val share: NotifyShare
) : AgentUpdate(chatId)