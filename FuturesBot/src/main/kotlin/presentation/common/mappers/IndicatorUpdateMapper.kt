package presentation.common.mappers

import domain.analysis.model.BollingerBandsData
import domain.math.MathUtil
import domain.updateService.updates.IndicatorUpdateData
import presentation.common.formatAndTrim

object IndicatorUpdateMapper {
    fun convertToText(data: IndicatorUpdateData): String {
        return when (data) {
            is IndicatorUpdateData.RsiHighData -> rsiHighDataMapper(data)
            is IndicatorUpdateData.RsiLowData -> rsiLowDataMapper(data)
            is IndicatorUpdateData.BbAboveData -> bbAboveDataMapper(data)
            is IndicatorUpdateData.BbBelowData -> bbBelowData(data)
        }
    }

    private fun rsiHighDataMapper(data: IndicatorUpdateData.RsiHighData): String {
        return "Rsi выше ${MathUtil.RSI_HIGH.formatAndTrim(2)}\n" +
                onCandles(daily = data.dailyRsi, hourly = data.hourlyRsi)
    }

    private fun rsiLowDataMapper(data: IndicatorUpdateData.RsiLowData): String {
        return "Rsi ниже ${MathUtil.RSI_LOW.formatAndTrim(2)}\n" +
                onCandles(daily = data.dailyRsi, hourly = data.hourlyRsi)
    }

    private fun onCandles(daily: Double, hourly: Double): String {
        return "По дневным свечам: ${daily.formatAndTrim(2)}\n" +
                "По часовым свечам ${hourly.formatAndTrim(2)}"
    }

    private fun bbAboveDataMapper(data: IndicatorUpdateData.BbAboveData): String {
        return "Полосы Боллинджера выше цены\n" +
                onBb(daily = data.dailyBb, hourly = data.hourlyBb)
    }

    private fun bbBelowData(data: IndicatorUpdateData.BbBelowData): String {
        return "Полосы Боллинджера ниже цены\n" +
                onBb(daily = data.dailyBb, hourly = data.hourlyBb)
    }

    private fun onBb(daily: BollingerBandsData, hourly: BollingerBandsData): String {
        return "Дневные полосы: ${daily.lower.formatAndTrim(2)} - ${daily.upper.formatAndTrim(2)}\n" +
                "Часовые полосы: ${hourly.lower.formatAndTrim(2)} - ${hourly.upper.formatAndTrim(2)}"
    }
}