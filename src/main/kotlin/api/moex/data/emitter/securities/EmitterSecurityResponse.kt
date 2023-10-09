package api.moex.data.emitter.securities

import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonContentPolymorphicSerializer
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.jsonObject

@Serializable(with = SecurityResponseSerializer::class)
sealed class EmitterSecurityResponse {
    @SerialName("charsetinfo")
    abstract val charsetInfo: CharsetInfo?

    abstract val securities: List<EmitterSecurity>?
}

@Serializable
data class CharsetInfoHolder(
    override val charsetInfo: CharsetInfo? = null,
    override val securities: List<EmitterSecurity>? = null
) : EmitterSecurityResponse()

@Serializable
data class DataHolder(
    override val charsetInfo: CharsetInfo? = null,
    override val securities: List<EmitterSecurity> = emptyList()
) : EmitterSecurityResponse()

@Serializable
data class CharsetInfo(
    val name: String,
)

object SecurityResponseSerializer :
    JsonContentPolymorphicSerializer<EmitterSecurityResponse>(EmitterSecurityResponse::class) {
    override fun selectDeserializer(element: JsonElement): DeserializationStrategy<EmitterSecurityResponse> {
        return when {
            element.jsonObject.containsKey("securities") -> DataHolder.serializer()
            element.jsonObject.containsKey("charsetinfo") -> CharsetInfoHolder.serializer()
            else -> throw Exception("Unknown Module: key 'type' not found or does not matches any module type")
        }
    }
}
