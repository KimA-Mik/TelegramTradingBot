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
        val min15Close = ClosePriceIndicator(seriesResult.min15)
        val hourlyClose = ClosePriceIndicator(seriesResult.hourly)
        val dailyClose = ClosePriceIndicator(seriesResult.daily)
        val min15RsiInd = RSIIndicator(min15Close, rsiPeriod)
        val hourlyRsiInd = RSIIndicator(hourlyClose, rsiPeriod)
        val dailyRsiInd = RSIIndicator(dailyClose, rsiPeriod)

        return CacheEntry(
            min15Rsi = min15RsiInd.lastDouble(),
            hourlyRsi = hourlyRsiInd.lastDouble(),
            dailyRsi = dailyRsiInd.lastDouble(),
            min15bb = BollingerBands.calculate(seriesResult.min15),
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

        interval = CandleInterval.CANDLE_INTERVAL_15_MIN
        val min15 = cacheServerApi.getMaxAmountOfHistoricCandles(uid, interval)
            .getOrElse { return Result.failure(it) }
            .toSeries(interval.duration, "15min")

        return Result.success(InitialSeries(hourly, daily, min15))
    }

    private data class InitialSeries(
        val hourly: BarSeries,
        val daily: BarSeries,
        val min15: BarSeries
    )
}