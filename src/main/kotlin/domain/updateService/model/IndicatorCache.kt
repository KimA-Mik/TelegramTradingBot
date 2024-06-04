package domain.updateService.model

data class IndicatorCache(
    val prices: Map<String, Double> = emptyMap(),
    val hourlyRsiCache: Map<String, Double> = emptyMap(),
    val dailyRsiCache: Map<String, Double> = emptyMap(),
)
