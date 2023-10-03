package api.investing

import kotlinx.serialization.Serializable

@Serializable
data class SearchResponse(val quotes: List<InvestingSecurity>)
