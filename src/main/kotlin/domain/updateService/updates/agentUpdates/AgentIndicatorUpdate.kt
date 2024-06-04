package domain.updateService.updates.agentUpdates

import domain.updateService.updates.IndicatorUpdateData

class AgentIndicatorUpdate(
    chatId: String,
    val ticker: String,
    val price: Double,
    val data: List<IndicatorUpdateData>
) : AgentUpdate(chatId)