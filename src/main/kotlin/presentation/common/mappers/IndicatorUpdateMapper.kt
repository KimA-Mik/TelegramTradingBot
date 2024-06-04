package presentation.common.mappers

import domain.math.MathUtil
import domain.updateService.updates.IndicatorUpdate
import presentation.common.formatAndTrim

object IndicatorUpdateMapper {
    fun convertToText(data: IndicatorUpdate.IndicatorUpdateData): String {
        return when (data) {
            is IndicatorUpdate.IndicatorUpdateData.RsiHighData -> rsiHighDataMapper(data)
            is IndicatorUpdate.IndicatorUpdateData.RsiLowData -> rsiLowDataMapper(data)
        }
    }

    private fun rsiHighDataMapper(data: IndicatorUpdate.IndicatorUpdateData.RsiHighData): String {
        return "Rsi выше ${MathUtil.RSI_HIGH.formatAndTrim(2)}\n" +
                onCandles(daily = data.dailyRsi, hourly = data.hourlyRsi)
    }

    private fun rsiLowDataMapper(data: IndicatorUpdate.IndicatorUpdateData.RsiLowData): String {
        return "Rsi ниже ${MathUtil.RSI_LOW.formatAndTrim(2)}\n" +
                onCandles(daily = data.dailyRsi, hourly = data.hourlyRsi)
    }

    private fun onCandles(daily: Double, hourly: Double): String {
        return "По дневным свечам: ${daily.formatAndTrim(2)}\n" +
                "По часовым свечам ${hourly.formatAndTrim(2)}"
    }
}