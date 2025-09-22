package domain.tinkoff.model

import domain.utils.DateUtil
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

data class DisplayShare @OptIn(ExperimentalTime::class) constructor(
    val ticker: String,
    val name: String,
    val price: Double,
    val priceDateTime: LocalDateTime? = Clock.System.now().toLocalDateTime(DateUtil.timezoneMoscow),
    val futures: List<DisplayFuture>
)
