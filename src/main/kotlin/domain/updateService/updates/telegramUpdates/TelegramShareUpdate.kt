package domain.updateService.updates.telegramUpdates

import domain.updateService.model.NotifyShare

class TelegramShareUpdate(
    userId: Long,
    val share: NotifyShare
) : TelegramUpdate(userId)
