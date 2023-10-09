package api.moex.data.security

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SecurityEntry(
    @SerialName("SECID") val secId: String,
    @SerialName("BOARDID") val boardId: String,
    @SerialName("SHORTNAME") val shortName: String,
    @SerialName("PREVPRICE") val prevPrice: Double,
    @SerialName("LOTSIZE") val lotSize: Int? = null,
    @SerialName("FACEVALUE") val faceValue: Double? = null,
    @SerialName("STATUS") val status: String? = null,
    @SerialName("BOARDNAME") val boardName: String? = null,
    @SerialName("DECIMALS") val decimals: Int,
    @SerialName("SECNAME") val secName: String,
    @SerialName("REMARKS") val remarks: String? = null,
    @SerialName("MARKETCODE") val marketCode: String? = null,
    @SerialName("INSTRID") val instrId: String? = null,
    @SerialName("SECTORID") val sectorId: String? = null,
    @SerialName("MINSTEP") val minStep: Double,
    @SerialName("PREVWAPRICE") val prevWaPrice: Double? = null,
    @SerialName("FACEUNIT") val faceUnit: String? = null,
    @SerialName("PREVDATE") val prevDate: String? = null, //Date
    @SerialName("ISSUESIZE") val issueSize: Long? = null,
    @SerialName("ISIN") val isIn: String? = null,
    @SerialName("LATNAME") val latName: String,
    @SerialName("REGNUMBER") val regNumber: String? = null,
    @SerialName("PREVLEGALCLOSEPRICE") val prevLegalClosePrice: Double? = null,
    @SerialName("CURRENCYID") val currencyId: String? = null,
    @SerialName("SECTYPE") val secType: String,
    @SerialName("LISTLEVEL") val listLevel: Int? = null,
    @SerialName("SETTLEDATE") val settleDate: String? = null //Date format 2023-10-09
)
