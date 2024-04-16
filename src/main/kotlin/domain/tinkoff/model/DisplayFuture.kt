package domain.tinkoff.model

import domain.utils.DateUtil
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.toLocalDateTime

data class DisplayFuture(
    val ticker: String,
    val name: String,
    val price: Double,
    val priceDateTime: LocalDateTime? = Clock.System.now().toLocalDateTime(DateUtil.timezoneMoscow),
    val percent: Double,
    val annualPercent: Double
)