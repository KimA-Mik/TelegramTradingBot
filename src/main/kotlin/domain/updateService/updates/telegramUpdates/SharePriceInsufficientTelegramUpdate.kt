package domain.updateService.updates.telegramUpdates

import domain.updateService.model.NotifyShare

class SharePriceInsufficientTelegramUpdate(
    userId: Long,
    val share: NotifyShare
) : TelegramUpdate(userId)
