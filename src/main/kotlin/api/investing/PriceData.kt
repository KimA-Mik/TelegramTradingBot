package api.investing

import api.extension.DateAsLongSerializer
import kotlinx.serialization.Serializable
import java.util.*

@Serializable
data class PriceData(
    @Serializable(with = DateAsLongSerializer::class)
    val date: Date,
    val p1: Double,
    val p2: Double,
    val p3: Double,
    val p4: Double,
    val p5: Double,
    val p6: Double
)
