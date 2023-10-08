package api.moex.data.security

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class DataVersionEntry(
    @SerialName("data_version") val dataVersion: Int,
    @SerialName("seqnum") val seqNum: Long //Date 20231006235959
)
