package domain.updateservice

import domain.updateservice.indicators.CacheEntry
import domain.user.model.User
import ru.kima.cacheserver.api.schema.model.Security

sealed class TelegramUpdate(val userId: Long) {
    data class PriceAlert(
        val user: User,
        val security: Security,
        val currentPrice: Double,
        val currentDeviation: Double,
        val indicators: CacheEntry?
    ) : TelegramUpdate(user.id)

    data class RsiAlert(
        val user: User,
        val security: Security,
        val currentPrice: Double,
        val currentRsi: Double,
        val indicators: CacheEntry?
    ) : TelegramUpdate(user.id)
}