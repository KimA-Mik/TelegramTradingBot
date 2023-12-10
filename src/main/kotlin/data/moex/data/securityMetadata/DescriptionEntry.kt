package data.moex.data.securityMetadata

import data.moex.data.util.NullAsEmptyStringSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class DescriptionEntry(
    @SerialName("name") val name: String,
    @SerialName("title") val title: String,
    @SerialName("value") val value: String,
    @SerialName("type")
    @Serializable(with = NullAsEmptyStringSerializer::class)
    val type: String,
    @SerialName("sort_order") val sortOrder: Long,
    @SerialName("is_hidden") val isHidden: Long
)
