package domain.updateService.updates.agentUpdates

import domain.updateService.model.NotifyShare

class AgentShareUpdate(
    chatId: String,
    val share: NotifyShare
) : AgentUpdate(chatId)