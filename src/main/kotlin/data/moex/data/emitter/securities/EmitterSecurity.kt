package data.moex.data.emitter.securities

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class EmitterSecurity(
    @SerialName("SECURITY_TYPE") val securityType: String,
    @SerialName("SECURITY_TYPE_ID") val securityTypeId: Int,
    @SerialName("TYPE") val type: String,
    @SerialName("SECID") val secId: String,
    @SerialName("SHORTNAME") val shortName: String,
    @SerialName("NAME") val name: String,
    @SerialName("REGNUMBER") val regNumber: String?,
    @SerialName("IS_TRADED") val isTraded: Int, //Bool
    @SerialName("PRIMARY_BOARDID") val primaryBoardId: String,
    @SerialName("MARKET") val market: String,
    @SerialName("ENGINE") val engine: String,
    @SerialName("FACEVALUE") val faceValue: Double?,
    @SerialName("FACEUNIT") val faceUnit: String?,
    @SerialName("ISIN") val isIn: String?
)
