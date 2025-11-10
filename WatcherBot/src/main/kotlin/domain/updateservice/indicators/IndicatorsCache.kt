package domain.updateservice.indicators

import org.ta4j.core.BarSeries
import org.ta4j.core.indicators.RSIIndicator
import org.ta4j.core.indicators.helpers.ClosePriceIndicator
import org.ta4j.core.indicators.volume.MoneyFlowIndexIndicator
import ru.kima.cacheserver.api.api.CacheServerApi
import ru.kima.cacheserver.api.schema.marketdataService.CandleInterval
import ru.kima.telegrambot.common.techanalysis.BollingerBands
import ru.kima.telegrambot.common.techanalysis.mappers.toSeries
import ru.kima.telegrambot.common.util.lastDouble
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
        val hour4Close = ClosePriceIndicator(seriesResult.hour4)
        val dailyClose = ClosePriceIndicator(seriesResult.daily)
        val min15RsiInd = RSIIndicator(min15Close, rsiPeriod)
        val hourlyRsiInd = RSIIndicator(hourlyClose, rsiPeriod)
        val hour4RsiInd = RSIIndicator(hour4Close, rsiPeriod)
        val dailyRsiInd = RSIIndicator(dailyClose, rsiPeriod)

        return CacheEntry(
            min15Rsi = min15RsiInd.lastDouble(),
            hourlyRsi = hourlyRsiInd.lastDouble(),
            hour4Rsi = hour4RsiInd.lastDouble(),
            dailyRsi = dailyRsiInd.lastDouble(),
            min15bb = BollingerBands.calculate(seriesResult.min15),
            hourlyBb = BollingerBands.calculate(seriesResult.hourly),
            hour4Bb = BollingerBands.calculate(seriesResult.hour4),
            dailyBb = BollingerBands.calculate(seriesResult.daily),
            min15Mfi = MoneyFlowIndexIndicator(seriesResult.min15, rsiPeriod).lastDouble(),
            hourlyMfi = MoneyFlowIndexIndicator(seriesResult.hourly, rsiPeriod).lastDouble(),
            hour4Mfi = MoneyFlowIndexIndicator(seriesResult.hour4, rsiPeriod).lastDouble(),
            dailyMfi = MoneyFlowIndexIndicator(seriesResult.daily, rsiPeriod).lastDouble()
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