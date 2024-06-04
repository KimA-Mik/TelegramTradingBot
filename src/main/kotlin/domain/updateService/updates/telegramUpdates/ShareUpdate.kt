package domain.updateService.updates.telegramUpdates

import domain.updateService.model.NotifyShare

class ShareUpdate(
    userId: Long,
    val share: NotifyShare
) : Update(userId)
