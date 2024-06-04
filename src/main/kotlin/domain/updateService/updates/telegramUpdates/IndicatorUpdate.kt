package domain.updateService.updates.telegramUpdates

import domain.updateService.updates.IndicatorUpdateData

class IndicatorUpdate(
    userId: Long,
    val ticker: String,
    val price: Double,
    val data: List<IndicatorUpdateData>
) : Update(userId)