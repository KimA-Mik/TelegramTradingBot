package domain.user.model

import kotlin.time.ExperimentalTime
import kotlin.time.Instant

data class User @OptIn(ExperimentalTime::class) constructor(
    val id: Long,
    val path: String,
    val registered: Instant
)
