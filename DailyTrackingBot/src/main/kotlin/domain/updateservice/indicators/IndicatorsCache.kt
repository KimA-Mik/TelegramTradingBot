package domain.updateservice.indicators

import domain.techanalysis.BollingerBands
import domain.techanalysis.mappers.toSeries
import domain.util.lastDouble
import org.ta4j.core.BarSeries
import org.ta4j.core.indicators.RSIIndicator
import org.ta4j.core.indicators.helpers.ClosePriceIndicator
import ru.kima.cacheserver.api.api.CacheServerApi
import ru.kima.cacheserver.api.schema.marketdataService.CandleInterval
import kotlin.time.ExperimentalTime

class IndicatorsCache(
    private val cacheServerApi: CacheServerApi,
) {
    private val cache = mutableMapOf<String, CacheEntry?>()

    suspend operator fun get(uid: String): CacheEntry? {
        if (cache.containsKey(uid)) {
            return cache[uid]
        } else {
            val indicators = calculateIndicators(uid)
            cache[uid] = indicators
            return indicators
        }
    }

    private suspend fun calculateIndicators(uid: String): CacheEntry? {
        val seriesResult = fetchAllSeries(uid).getOrElse { return null }
        if (seriesResult.hourly.barCount == 0 || seriesResult.daily.barCount == 0) return null

        // RSI
        val rsiPeriod = 14
        val hourlyClose = ClosePriceIndicator(seriesResult.hourly)
        val dailyClose = ClosePriceIndicator(seriesResult.daily)
        val hourlyRsiInd = RSIIndicator(hourlyClose, rsiPeriod)
        val dailyRsiInd = RSIIndicator(dailyClose, rsiPeriod)

        return CacheEntry(
            hourlyRsi = hourlyRsiInd.lastDouble(),
            dailyRsi = dailyRsiInd.lastDouble(),
            hourlyBb = BollingerBands.calculate(seriesResult.hourly),
            dailyBb = BollingerBands.calculate(seriesResult.daily),
        )
    }

    /**
     * Запрашивает дневные и часовые свечи с максимальным количеством данных для каждого интервала.
     */
    @OptIn(ExperimentalTime::class)
    private suspend fun fetchAllSeries(uid: String): Result<InitialSeries> {
        var interval: CandleInterval = CandleInterval.CANDLE_INTERVAL_HOUR
        val hourly = cacheServerApi.getMaxAmountOfHistoricCandles(uid, interval)
            .getOrElse { return Result.failure(it) }
            .toSeries(interval.duration, "hourly")
        interval = CandleInterval.CANDLE_INTERVAL_DAY
        val daily = cacheServerApi.getMaxAmountOfHistoricCandles(uid, interval)
            .getOrElse { return Result.failure(it) }
            .toSeries(interval.duration, "daily")
        return Result.success(InitialSeries(hourly, daily))
    }

    private data class InitialSeries(
        val hourly: BarSeries,
        val daily: BarSeries
    )
}