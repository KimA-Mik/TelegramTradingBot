package domain.updateService.updates

import domain.updateService.model.NotifyShare

class SharePriceInsufficientUpdate(
    userId: Long,
    val share: NotifyShare
) : Update(userId)
