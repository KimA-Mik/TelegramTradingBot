package domain.user.model

import domain.common.PATH_SEPARATOR
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

data class User @OptIn(ExperimentalTime::class) constructor(
    val id: Long,
    val path: String,
    val registered: Instant,
    val defaultPriceProlongation: PriceProlongation,
    val enableSrsi: Boolean,
    val timeframesToFire: Int,
) {
    val pathList by lazy { path.split(PATH_SEPARATOR).drop(1) }
}
