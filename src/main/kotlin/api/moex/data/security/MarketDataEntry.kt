package api.moex.data.security

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MarketDataEntry(
    @SerialName("SECID") val secId: String,
    @SerialName("BOARDID") val boardId: String,
    @SerialName("BID") val bid: Double?,
    @SerialName("OFFER") val offer: Double?,
    @SerialName("SPREAD") val spread: Double,
    @SerialName("BIDDEPTHT") val bidDepth: Int?, //with typo
    @SerialName("OFFERDEPTHT") val offerDepth: Int?, //with typo
    @SerialName("OPEN") val open: Double,
    @SerialName("LOW") val low: Double,
    @SerialName("HIGH") val high: Double,
    @SerialName("LAST") val last: Double,
    @SerialName("LASTCHANGE") val lastChange: Double,
    @SerialName("LASTCHANGEPRCNT") val lastChangePercent: Double,
    @SerialName("QTY") val qty: Int? = null, //futures
    @SerialName("VALUE") val value: Double? = null, // futures
    @SerialName("VALUE_USD") val valueUsd: Double? = null, //futures
    @SerialName("WAPRICE") val waPrice: Double? = null, //futures
    @SerialName("LASTCNGTOLASTWAPRICE") val lastCngToLastWaPrice: Double? = null, //futures
    @SerialName("WAPTOPREVWAPRICEPRCNT") val wapToPrevWaPricePercent: Double? = null, //futures
    @SerialName("WAPTOPREVWAPRICE") val wapToPrevWaPrice: Double? = null, //futures
    @SerialName("CLOSEPRICE") val closePrice: Double? = null,
    @SerialName("MARKETPRICETODAY") val marketPriceToday: Double? = null,
    @SerialName("MARKETPRICE") val marketPrice: Double? = null, //futures
    @SerialName("LASTTOPREVPRICE") val lastToPrevPrice: Double? = null,
    @SerialName("NUMTRADES") val numTrades: Int,
    @SerialName("VOLTODAY") val volToday: Long,
    @SerialName("VALTODAY") val valToday: Long,
    @SerialName("VALTODAY_USD") val valTodayUsd: Long,
    @SerialName("ETFSETTLEPRICE") val etfSettlePrice: Double? = null,
    @SerialName("TRADINGSTATUS") val tradingStatus: String? = null, //futures
    @SerialName("UPDATETIME") val updateTime: String, // time "23:50:07"
    @SerialName("LCLOSEPRICE") val lClosePrice: Double? = null,
    @SerialName("LCURRENTPRICE") val lCurrentPrice: Double? = null, //futures
    @SerialName("MARKETPRICE2") val marketPrice2: Double? = null,
    @SerialName("CHANGE") val change: Double? = null, //futures
    @SerialName("TIME") val time: String, // time "23:49:58"
    @SerialName("PRICEMINUSPREVWAPRICE") val priceMinusPrevWaPrice: Double? = null, //futures
    @SerialName("OPENPERIODPRICE") val openPeriodPrice: Double? = null,
    @SerialName("SEQNUM") val seqNum: Long,
    @SerialName("SYSTIME") val sysTime: String, //DateTime "2023-10-08 00:05:00"
    @SerialName("CLOSINGAUCTIONPRICE") val closingAuctionPrice: Double? = null,
    @SerialName("CLOSINGAUCTIONVOLUME") val closingAuctionVolume: Double? = null,
    @SerialName("ISSUECAPITALIZATION") val issueCapitalization: Double? = null, //futures
    @SerialName("ISSUECAPITALIZATION_UPDATETIME") val issueCapitalizationUpdateTime: String? = null,//futures //Time "23:49:58"
    @SerialName("ETFSETTLECURRENCY") val etfSettleCurrency: String? = null,
    @SerialName("VALTODAY_RUR") val valTodayRur: Long? = null, //futures
    @SerialName("TRADINGSESSION") val tradingSession: String? = null
)
