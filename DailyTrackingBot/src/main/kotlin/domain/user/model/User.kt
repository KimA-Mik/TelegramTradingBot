package domain.user.model

import domain.common.PATH_SEPARATOR
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
    val showNote: Boolean,
    val shouldNotify: Boolean,
) {
    val pathList by lazy { path.split(PATH_SEPARATOR).drop(1) }
}
