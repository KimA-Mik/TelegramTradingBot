package domain.updateservice.indicators

import domain.techanalysis.BollingerBands


data class CacheEntry(
    val hourlyRsi: Double,
    val dailyRsi: Double,
    val hourlyBb: BollingerBands.BollingerBandsData,
    val dailyBb: BollingerBands.BollingerBandsData
)
