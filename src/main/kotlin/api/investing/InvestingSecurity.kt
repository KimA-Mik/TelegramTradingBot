package api.investing

import kotlinx.serialization.Serializable

@Serializable
data class InvestingSecurity(
    val id: Int,
    val description: String,
    val symbol: String
)
