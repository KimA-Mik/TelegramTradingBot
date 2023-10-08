package api.moex.data.security

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SecurityEntry(
    @SerialName("SECID") val secId: String,
    @SerialName("BOARDID") val boardId: String,
    @SerialName("SHORTNAME") val shortName: String,
    @SerialName("PREVPRICE") val prevPrice: Double,
    @SerialName("LOTSIZE") val lotSize: Int,
    @SerialName("FACEVALUE") val faceValue: Double,
    @SerialName("STATUS") val status: String,
    @SerialName("BOARDNAME") val boardName: String,
    @SerialName("DECIMALS") val decimals: Int,
    @SerialName("SECNAME") val secName: String,
    @SerialName("REMARKS") val remarks: String?,
    @SerialName("MARKETCODE") val marketCode: String,
    @SerialName("INSTRID") val instrId: String,
    @SerialName("SECTORID") val sectorId: String?,
    @SerialName("MINSTEP") val minStep: Double,
    @SerialName("PREVWAPRICE") val prevWaPrice: Double,
    @SerialName("FACEUNIT") val faceUnit: String,
    @SerialName("PREVDATE") val prevDate: String, //Date
    @SerialName("ISSUESIZE") val issueSize: Long,
    @SerialName("ISIN") val isIn: String,
    @SerialName("LATNAME") val latName: String,
    @SerialName("REGNUMBER") val regNumber: String,
    @SerialName("PREVLEGALCLOSEPRICE") val prevLegalClosePrice: Double,
    @SerialName("CURRENCYID") val currencyId: String,
    @SerialName("SECTYPE") val secType: String,
    @SerialName("LISTLEVEL") val listLevel: Int,
    @SerialName("SETTLEDATE") val settleDate: String //Date format 2023-10-09
)
