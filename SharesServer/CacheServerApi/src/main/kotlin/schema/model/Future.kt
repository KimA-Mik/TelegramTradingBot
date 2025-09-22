package ru.kima.cacheserver.api.schema.model

import kotlinx.serialization.Serializable
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

@Serializable
data class Future @OptIn(ExperimentalTime::class) constructor(
    val uid: String = String(),
    val ticker: String = String(),
    val name: String = String(),
    val lot: Int = 0,
    val expirationDate: Instant
)
