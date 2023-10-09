package api.moex.data.securityMetadata

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class DescriptionEntry(
    @SerialName("name") val name: String,
    @SerialName("title") val title: String,
    @SerialName("value") val value: String,
    @SerialName("type") val type: String,
    @SerialName("sort_order") val sortOrder: Long,
    @SerialName("is_hidden") val isHidden: Long
)
