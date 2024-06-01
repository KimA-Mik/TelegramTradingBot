package domain.updateService.model

data class IndicatorCache(
    val hourlyRsiCache: Map<String, Double> = emptyMap(),
    val dailyRsiCache: Map<String, Double> = emptyMap(),
)
