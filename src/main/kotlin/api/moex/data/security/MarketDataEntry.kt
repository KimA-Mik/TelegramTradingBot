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
    @SerialName("BIDDEPTHT") val bidDepth: Int, //with typo
    @SerialName("OFFERDEPTHT") val offerDepth: Int, //with typo
    @SerialName("OPEN") val open: Double,
    @SerialName("LOW") val low: Double,
    @SerialName("HIGH") val high: Double,
    @SerialName("LAST") val last: Double,
    @SerialName("LASTCHANGE") val lastChange: Double,
    @SerialName("LASTCHANGEPRCNT") val lastChangePercent: Double,
    @SerialName("QTY") val qty: Int,
    @SerialName("VALUE") val value: Double,
    @SerialName("VALUE_USD") val valueUsd: Double,
    @SerialName("WAPRICE") val waPrice: Double,
    @SerialName("LASTCNGTOLASTWAPRICE") val lastCngToLastWaPrice: Double,
    @SerialName("WAPTOPREVWAPRICEPRCNT") val wapToPrevWaPricePercent: Double,
    @SerialName("WAPTOPREVWAPRICE") val wapToPrevWaPrice: Double,
    @SerialName("CLOSEPRICE") val closePrice: Double?,
    @SerialName("MARKETPRICETODAY") val marketPriceToday: Double,
    @SerialName("MARKETPRICE") val marketPrice: Double,
    @SerialName("LASTTOPREVPRICE") val lastToPrevPrice: Double,
    @SerialName("NUMTRADES") val numTrades: Int,
    @SerialName("VOLTODAY") val volToday: Long,
    @SerialName("VALTODAY") val valToday: Long,
    @SerialName("VALTODAY_USD") val valTodayUsd: Long,
    @SerialName("ETFSETTLEPRICE") val etfSettlePrice: Double?,
    @SerialName("TRADINGSTATUS") val tradingStatus: String,
    @SerialName("UPDATETIME") val updateTime: String, // time "23:50:07"
    @SerialName("LCLOSEPRICE") val lClosePrice: Double,
    @SerialName("LCURRENTPRICE") val lCurrentPrice: Double,
    @SerialName("MARKETPRICE2") val marketPrice2: Double?,
    @SerialName("CHANGE") val change: Double,
    @SerialName("TIME") val time: String, // time "23:49:58"
    @SerialName("PRICEMINUSPREVWAPRICE") val priceMinusPrevWaPrice: Double,
    @SerialName("OPENPERIODPRICE") val openPeriodPrice: Double?,
    @SerialName("SEQNUM") val seqNum: Long,
    @SerialName("SYSTIME") val sysTime: String, //DateTime "2023-10-08 00:05:00"
    @SerialName("CLOSINGAUCTIONPRICE") val closingAuctionPrice: Double,
    @SerialName("CLOSINGAUCTIONVOLUME") val closingAuctionVolume: Double?,
    @SerialName("ISSUECAPITALIZATION") val issueCapitalization: Double,
    @SerialName("ISSUECAPITALIZATION_UPDATETIME") val issueCapitalizationUpdateTime: String, //Time "23:49:58"
    @SerialName("ETFSETTLECURRENCY") val etfSettleCurrency: String?,
    @SerialName("VALTODAY_RUR") val valTodayRur: Long,
    @SerialName("TRADINGSESSION") val tradingSession: String?
)
