package domain.updateService.updates

class IndicatorUpdate(
    userId: Long,
    val ticker: String,
    val price: Double,
    val data: List<IndicatorUpdateData>
) : Update(userId) {

    sealed interface IndicatorUpdateData {
        data class RsiHighData(val hourlyRsi: Double, val dailyRsi: Double) : IndicatorUpdateData
        data class RsiLowData(val hourlyRsi: Double, val dailyRsi: Double) : IndicatorUpdateData
    }
}