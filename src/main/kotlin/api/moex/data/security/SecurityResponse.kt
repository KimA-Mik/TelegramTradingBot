package api.moex.data.security

import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonContentPolymorphicSerializer
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.jsonObject

@Serializable(with = SecurityResponseSerializer::class)
sealed class SecurityResponse {
    @SerialName("charsetinfo")
    abstract val charsetInfo: CharsetInfo?

    @SerialName("securities")
    abstract val securities: List<SecurityEntry>?

    @SerialName("marketdata")
    abstract val marketData: List<MarketDataEntry>?

    @SerialName("dataversion")
    abstract val dataVersion: List<DataVersionEntry>?
}


@Serializable
data class CharsetInfoHolder(
    override val charsetInfo: CharsetInfo? = null,
    override val securities: List<SecurityEntry>? = null,
    override val marketData: List<MarketDataEntry>? = null,
    override val dataVersion: List<DataVersionEntry>? = null
) : SecurityResponse()

@Serializable
@SerialName("charsetinfo")
data class CharsetInfo(
    val name: String,
)

@Serializable
data class DataHolder(
    override val charsetInfo: CharsetInfo? = null,
    override val securities: List<SecurityEntry>,
    @SerialName("marketdata")
    override val marketData: List<MarketDataEntry>,
    @SerialName("dataversion")
    override val dataVersion: List<DataVersionEntry>
) : SecurityResponse()

object SecurityResponseSerializer : JsonContentPolymorphicSerializer<SecurityResponse>(SecurityResponse::class) {
    override fun selectDeserializer(element: JsonElement): DeserializationStrategy<SecurityResponse> {
        return when {
            element.jsonObject.containsKey("marketdata") -> DataHolder.serializer()
            element.jsonObject.containsKey("charsetinfo") -> CharsetInfoHolder.serializer()
            else -> throw Exception("Unknown Module: key 'type' not found or does not matches any module type")
        }
    }
}
