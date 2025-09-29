package domain.user.model

import kotlin.time.ExperimentalTime
import kotlin.time.Instant

data class User @OptIn(ExperimentalTime::class) constructor(
    val id: Long,
    val path: String,
    val registered: Instant,
    val ticker: String?,
    val targetPrice: Double?,
    val targetDeviation: Double?,
    val isActive: Boolean,
    val remainActive: Boolean,
    val securityConfigureSequence: Boolean,
    val note: String?,
    val showNote: Boolean
)
