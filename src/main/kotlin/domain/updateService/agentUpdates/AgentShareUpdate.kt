package domain.updateService.agentUpdates

import domain.updateService.model.NotifyShare

class AgentShareUpdate(
    chatId: String,
    val share: NotifyShare
) : AgentUpdate(chatId)