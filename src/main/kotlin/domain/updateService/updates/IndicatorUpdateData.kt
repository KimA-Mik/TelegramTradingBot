package domain.updateService.updates

import domain.analysis.model.BollingerBandsData

sealed interface IndicatorUpdateData {
    data class RsiHighData(val hourlyRsi: Double, val dailyRsi: Double) : IndicatorUpdateData
    data class RsiLowData(val hourlyRsi: Double, val dailyRsi: Double) : IndicatorUpdateData

    data class BbAboveData(val hourlyBb: BollingerBandsData, val dailyBb: BollingerBandsData) : IndicatorUpdateData
    data class BbBelowData(val hourlyBb: BollingerBandsData, val dailyBb: BollingerBandsData) : IndicatorUpdateData
}
