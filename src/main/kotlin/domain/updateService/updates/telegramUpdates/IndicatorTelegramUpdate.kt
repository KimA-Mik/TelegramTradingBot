package domain.updateService.updates.telegramUpdates

import domain.updateService.updates.IndicatorUpdateData

class IndicatorTelegramUpdate(
    userId: Long,
    val ticker: String,
    val price: Double,
    val data: List<IndicatorUpdateData>
) : TelegramUpdate(userId)