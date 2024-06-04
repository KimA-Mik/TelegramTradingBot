package presentation.common.mappers

import domain.math.MathUtil
import domain.updateService.updates.IndicatorUpdateData
import presentation.common.formatAndTrim

object IndicatorUpdateMapper {
    fun convertToText(data: IndicatorUpdateData): String {
        return when (data) {
            is IndicatorUpdateData.RsiHighData -> rsiHighDataMapper(data)
            is IndicatorUpdateData.RsiLowData -> rsiLowDataMapper(data)
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
}