package domain.config

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class LocalConfig(
    @SerialName("predefined_instruments")
    val securities: List<String> = emptyList(),
)
