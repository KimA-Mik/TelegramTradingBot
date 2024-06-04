package domain.updateService.updates.telegramUpdates

import domain.updateService.model.NotifyShare

class TelegramSharePriceInsufficientUpdate(
    userId: Long,
    val share: NotifyShare
) : TelegramUpdate(userId)
