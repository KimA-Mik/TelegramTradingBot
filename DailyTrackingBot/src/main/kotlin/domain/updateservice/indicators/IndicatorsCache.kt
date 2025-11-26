package domain.updateservice.indicators

import domain.techanalysis.BollingerBands
import domain.techanalysis.mappers.toSeries
import domain.util.lastDouble
import org.ta4j.core.BarSeries
import org.ta4j.core.indicators.RSIIndicator
import org.ta4j.core.indicators.StochasticRSIIndicator
import org.ta4j.core.indicators.helpers.ClosePriceIndicator
import org.ta4j.core.indicators.volume.MoneyFlowIndexIndicator
import ru.kima.cacheserver.api.api.CacheServerApi
import ru.kima.cacheserver.api.schema.marketdataService.CandleInterval
import kotlin.time.ExperimentalTime

private const val DEFAULT_BAR_COUNT = 14

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
        val min15Close = ClosePriceIndicator(seriesResult.min15)
        val hourlyClose = ClosePriceIndicator(seriesResult.hourly)
        val hour4Close = ClosePriceIndicator(seriesResult.hour4)
        val dailyClose = ClosePriceIndicator(seriesResult.daily)
        val min15RsiInd = RSIIndicator(min15Close, DEFAULT_BAR_COUNT)
        val hourlyRsiInd = RSIIndicator(hourlyClose, DEFAULT_BAR_COUNT)
        val hour4RsiInd = RSIIndicator(hour4Close, DEFAULT_BAR_COUNT)
        val dailyRsiInd = RSIIndicator(dailyClose, DEFAULT_BAR_COUNT)

        return CacheEntry(
            min15Rsi = min15RsiInd.lastDouble(),
            hourlyRsi = hourlyRsiInd.lastDouble(),
            hour4Rsi = hour4RsiInd.lastDouble(),
            dailyRsi = dailyRsiInd.lastDouble(),
            min15bb = BollingerBands.calculate(min15Close),
            hourlyBb = BollingerBands.calculate(hourlyClose),
            hour4Bb = BollingerBands.calculate(hour4Close),
            dailyBb = BollingerBands.calculate(dailyClose),
            min15Mfi = MoneyFlowIndexIndicator(seriesResult.min15, DEFAULT_BAR_COUNT).lastDouble(),
            hourlyMfi = MoneyFlowIndexIndicator(seriesResult.hourly, DEFAULT_BAR_COUNT).lastDouble(),
            hour4Mfi = MoneyFlowIndexIndicator(seriesResult.hour4, DEFAULT_BAR_COUNT).lastDouble(),
            dailyMfi = MoneyFlowIndexIndicator(seriesResult.daily, DEFAULT_BAR_COUNT).lastDouble(),
            min15Srsi = StochasticRSIIndicator(min15RsiInd, DEFAULT_BAR_COUNT).lastDouble(),
            hourlySrsi = StochasticRSIIndicator(hourlyRsiInd, DEFAULT_BAR_COUNT).lastDouble(),
            hour4Srsi = StochasticRSIIndicator(hour4RsiInd, DEFAULT_BAR_COUNT).lastDouble(),
            dailySrsi = StochasticRSIIndicator(dailyRsiInd, DEFAULT_BAR_COUNT).lastDouble(),
        )
    }

    /**
     * Запрашивает дневные и часовые свечи с максимальным количеством данных для каждого интервала.
     */
    @OptIn(ExperimentalTime::class)
    private suspend fun fetchAllSeries(uid: String): Result<InitialSeries> {
        var interval = CandleInterval.CANDLE_INTERVAL_15_MIN
        val min15 = cacheServerApi.getMaxAmountOfHistoricCandles(uid, interval)
            .getOrElse { return Result.failure(it) }
            .toSeries(interval.duration, "15min")

        interval = CandleInterval.CANDLE_INTERVAL_HOUR
        val hourly = cacheServerApi.getMaxAmountOfHistoricCandles(uid, interval)
            .getOrElse { return Result.failure(it) }
            .toSeries(interval.duration, "hourly")

        interval = CandleInterval.CANDLE_INTERVAL_4_HOUR
        val hours4 = cacheServerApi.getMaxAmountOfHistoricCandles(uid, interval)
            .getOrElse { return Result.failure(it) }
            .toSeries(interval.duration, "4hours")

        interval = CandleInterval.CANDLE_INTERVAL_DAY
        val daily = cacheServerApi.getMaxAmountOfHistoricCandles(uid, interval)
            .getOrElse { return Result.failure(it) }
            .toSeries(interval.duration, "daily")

        return Result.success(
            InitialSeries(min15 = min15, hour4 = hours4, hourly = hourly, daily = daily)
        )
    }

    private data class InitialSeries(
        val min15: BarSeries,
        val hourly: BarSeries,
        val hour4: BarSeries,
        val daily: BarSeries
    )
}