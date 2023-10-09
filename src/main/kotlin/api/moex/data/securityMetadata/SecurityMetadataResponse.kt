package api.moex.data.securityMetadata

import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonContentPolymorphicSerializer
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.jsonObject

@Serializable(with = SecurityResponseSerializer::class)
sealed class SecurityMetadataResponse {
    @SerialName("charsetinfo")
    abstract val charsetInfo: CharsetInfo?

    abstract val description: List<DescriptionEntry>?
    abstract val boards: List<BoardEntry>?
}

@Serializable
data class CharsetInfoHolder(
    @SerialName("charsetinfo")
    override val charsetInfo: CharsetInfo,
    override val description: List<DescriptionEntry>? = null,
    override val boards: List<BoardEntry>? = null
) : SecurityMetadataResponse()

@Serializable
@SerialName("charsetinfo")
data class CharsetInfo(
    val name: String,
)

@Serializable
data class DataHolder(
    @SerialName("charsetinfo")
    override val charsetInfo: CharsetInfo? = null,
    override val description: List<DescriptionEntry> = emptyList(),
    override val boards: List<BoardEntry> = emptyList()
) : SecurityMetadataResponse()

object SecurityResponseSerializer :
    JsonContentPolymorphicSerializer<SecurityMetadataResponse>(SecurityMetadataResponse::class) {
    override fun selectDeserializer(element: JsonElement): DeserializationStrategy<SecurityMetadataResponse> {
        return when {
            element.jsonObject.containsKey("description") -> DataHolder.serializer()
            element.jsonObject.containsKey("charsetinfo") -> CharsetInfoHolder.serializer()
            else -> throw Exception("Unknown Module: key 'type' not found or does not matches any module type")
        }
    }
}