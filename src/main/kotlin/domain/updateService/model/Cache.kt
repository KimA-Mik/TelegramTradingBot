package domain.updateService.model

import domain.analysis.model.BollingerBandsData
import domain.tinkoff.model.TinkoffFuture

data class Cache(
    val shares: Map<String, SecurityPrice> = emptyMap(),
    val sharesToFutures: Map<String, List<TinkoffFuture>> = emptyMap(),
    val futures: Map<String, SecurityPrice> = emptyMap(),
    val hourlyRsi: Map<String, Double> = emptyMap(),
    val dailyRsi: Map<String, Double> = emptyMap(),
    val hourlyBollingerBands: Map<String, BollingerBandsData> = emptyMap(),
    val dailyBollingerBands: Map<String, BollingerBandsData> = emptyMap(),
)
