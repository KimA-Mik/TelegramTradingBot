package domain.updateservice

import domain.updateservice.indicators.CacheEntry
import domain.user.model.TrackingSecurity
import domain.user.model.User

sealed class TelegramUpdate(val userId: Long) {
    data class PriceAlert(
        val user: User,
        val security: TrackingSecurity,
        val currentPrice: Double,
        val indicators: CacheEntry?,
        val type: PriceType
    ) : TelegramUpdate(user.id) {
        sealed class PriceType {
            abstract val deviation: Double

            data class Target(override val deviation: Double) : PriceType()
            data class LowTarget(override val deviation: Double) : PriceType()
            data class All(override val deviation: Double, val lowDeviation: Double) : PriceType()
        }
    }

    data class RsiAlert(
        val user: User,
        val security: TrackingSecurity,
        val currentPrice: Double,
        val intervals: List<RsiInterval>,
        val indicators: CacheEntry
    ) : TelegramUpdate(user.id) {
        enum class RsiInterval {
            MIN15, HOUR4
        }
    }

    data class BbAlert(
        val user: User,
        val security: TrackingSecurity,
        val currentPrice: Double,
        val intervals: List<BbInterval>,
        val indicators: CacheEntry
    ) : TelegramUpdate(user.id) {
        enum class BbInterval {
            MIN15, HOUR4
        }
    }
}