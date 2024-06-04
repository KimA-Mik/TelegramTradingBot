package domain.updateService.updates.telegramUpdates

import domain.updateService.model.NotifyShare

class ShareTelegramUpdate(
    userId: Long,
    val share: NotifyShare
) : TelegramUpdate(userId)
