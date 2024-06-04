package domain.updateService.updates

sealed interface IndicatorUpdateData {
    data class RsiHighData(val hourlyRsi: Double, val dailyRsi: Double) : IndicatorUpdateData
    data class RsiLowData(val hourlyRsi: Double, val dailyRsi: Double) : IndicatorUpdateData
}
