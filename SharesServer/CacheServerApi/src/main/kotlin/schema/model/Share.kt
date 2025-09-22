package ru.kima.cacheserver.api.schema.model

import kotlinx.serialization.Serializable

@Serializable
data class Share(
    val uid: String = String(),
    val ticker: String = String(),
    val name: String = String(),
    val lot: Int = 0
)
