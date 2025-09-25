package ru.kima.cacheserver.api.schema.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


@Serializable
@SerialName("Share")
data class Share(
    override val uid: String = String(),
    val ticker: String = String(),
    val name: String = String(),
    val lot: Int = 0
) : Security()
