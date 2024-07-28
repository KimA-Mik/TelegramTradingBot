package domain.updateService.model

import domain.tinkoff.model.TinkoffFuture

data class Cache(
    val shares: Map<String, Double> = emptyMap(),
    val sharesToFutures: Map<String, List<TinkoffFuture>> = emptyMap(),
    val futures: Map<String, Double> = emptyMap(),
    val hourlyRsiCache: Map<String, Double> = emptyMap(),
    val dailyRsiCache: Map<String, Double> = emptyMap(),
)
