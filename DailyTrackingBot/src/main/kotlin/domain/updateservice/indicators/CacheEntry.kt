package domain.updateservice.indicators

import domain.techanalysis.BollingerBands


data class CacheEntry(
    val min15Rsi: Double,
    val hourlyRsi: Double,
    val hour4Rsi: Double,
    val dailyRsi: Double,
    val min15bb: BollingerBands.BollingerBandsData,
    val hourlyBb: BollingerBands.BollingerBandsData,
    val hour4Bb: BollingerBands.BollingerBandsData,
    val dailyBb: BollingerBands.BollingerBandsData
)
