package ru.kima.cacheserver.api.schema.model

import kotlinx.serialization.Serializable
import ru.kima.cacheserver.api.schema.marketdataService.LastPriceType
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

@Serializable
data class LastPrice @OptIn(ExperimentalTime::class) constructor(
    val uid: String,
    val price: Double,
    val time: Instant,
    val lastPriceType: LastPriceType
)
