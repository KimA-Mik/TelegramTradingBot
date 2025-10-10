package ru.kima.cacheserver.api.schema.model

import kotlinx.serialization.Serializable

@Serializable
sealed class Security {
    abstract val uid: String
    abstract val ticker: String
    abstract val name: String
}
