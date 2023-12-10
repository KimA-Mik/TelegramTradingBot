package data.moex.data.securityMetadata

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class BoardEntry(
    @SerialName("secid") val secId: String,
    @SerialName("boardid") val boardId: String,
    @SerialName("title") val title: String,
    @SerialName("board_group_id") val boardGroupId: Int,
    @SerialName("market_id") val marketId: Int,
    @SerialName("market") val market: String,
    @SerialName("engine_id") val engineId: Int,
    @SerialName("engine") val engine: String,
    @SerialName("is_traded") val isTraded: Int,
    @SerialName("decimals") val decimals: Int,
    @SerialName("history_from") val historyFrom: String?, //date "2014-06-09"
    @SerialName("history_till") val historyTill: String?, //date "2014-06-09"
    @SerialName("listed_from") val listedFrom: String, //date "2014-06-09"
    @SerialName("listed_till") val listedTill: String, //date "2014-06-09"
    @SerialName("is_primary") val isPrimary: Int,
    @SerialName("currencyid") val currencyId: String?
)
