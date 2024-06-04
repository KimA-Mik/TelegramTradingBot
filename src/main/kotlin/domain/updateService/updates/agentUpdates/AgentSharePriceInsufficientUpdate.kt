package domain.updateService.updates.agentUpdates

import domain.updateService.model.NotifyShare

class AgentSharePriceInsufficientUpdate(
    chatId: String,
    val share: NotifyShare
) : AgentUpdate(chatId)