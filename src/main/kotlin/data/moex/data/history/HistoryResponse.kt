package data.moex.data.history

import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonContentPolymorphicSerializer
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.jsonObject

@Serializable(with = HistoryResponseSerializer::class)
sealed class HistoryResponse {
    @SerialName("charsetinfo")
    abstract val charsetInfo: CharsetInfo?

    @SerialName("history")
    abstract val history: List<HistoryEntry>?
}

@Serializable
data class HistoryHolder(
    override val history: List<HistoryEntry>? = null,
    override val charsetInfo: CharsetInfo? = null
) : HistoryResponse()

@Serializable
@SerialName("history")
data class HistoryEntry(
    @SerialName("BOARDID") val boardId: String,
    @SerialName("TRADEDATE") val tradeDate: String,
    @SerialName("SHORTNAME") val shortName: String,
    @SerialName("SECID") val secId: String,
    @SerialName("NUMTRADES") val numTrades: Double,
    @SerialName("VALUE") val value: Double,
    @SerialName("OPEN") val open: Double,
    @SerialName("LOW") val low: Double,
    @SerialName("HIGH") val high: Double,
    @SerialName("LEGALCLOSEPRICE") val legalClosePrice: Double,
    @SerialName("WAPRICE") val waPrice: Double,
    @SerialName("CLOSE") val close: Double,
    @SerialName("VOLUME") val volume: Double,
    @SerialName("MARKETPRICE2") val marketPrice2: Double,
    @SerialName("MARKETPRICE3") val marketPrice3: Double,
    @SerialName("ADMITTEDQUOTE") val admittedQuote: Double,
    @SerialName("MP2VALTRD") val mp2ValTrd: Double,
    @SerialName("MARKETPRICE3TRADESVALUE") val marketPrice3TradesValue: Double,
    @SerialName("ADMITTEDVALUE") val admittedValue: Double,
    @SerialName("WAVAL") val waval: Double?,
    @SerialName("TRADINGSESSION") val tradingSession: Double,
    @SerialName("CURRENCYID") val currencyId: String,
    @SerialName("TRENDCLSPR") val trendClSpr: Double?
)

@Serializable
data class CharsetInfoHolder(
    override val charsetInfo: CharsetInfo? = null,
    override val history: List<HistoryEntry>? = null

) : HistoryResponse()

@Serializable
@SerialName("charsetinfo")
data class CharsetInfo(
    val name: String,
)

object HistoryResponseSerializer : JsonContentPolymorphicSerializer<HistoryResponse>(HistoryResponse::class) {
    override fun selectDeserializer(element: JsonElement): DeserializationStrategy<HistoryResponse> {
        return when {
            element.jsonObject.containsKey("history") -> HistoryHolder.serializer()
            element.jsonObject.containsKey("charsetinfo") -> CharsetInfoHolder.serializer()
            else -> throw Exception("Unknown Module: key 'type' not found or does not matches any module type")
        }
    }
}
