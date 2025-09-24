package ru.kima.cacheserver.api.util

inline fun <reified T : Enum<T>> enumValueOfOrNull(name: String?): T? =
    name?.runCatching { enumValueOf<T>(this) }?.getOrNull()
