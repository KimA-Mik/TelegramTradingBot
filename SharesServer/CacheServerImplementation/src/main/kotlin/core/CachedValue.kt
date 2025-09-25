package ru.kima.cacheserver.implementation.core

import kotlin.time.Clock
import kotlin.time.Duration
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

@OptIn(ExperimentalTime::class)
class CachedValue<T>(
    private val cacheLifetime: Duration,
    private val updateMethod: suspend () -> T
) {
    private var cacheUpdated: Instant = Clock.System.now()
    private var cachedValue: T? = null

    suspend fun getValue(): Result<T> {
        if (cachedValue == null ||
            (Clock.System.now() - cacheUpdated) > cacheLifetime
        ) {
            try {
                cachedValue = updateMethod()
            } catch (e: Exception) {
                cachedValue?.let { return Result.success(it) }
                return Result.failure(e)
            }
        }

        cachedValue?.let { return Result.success(it) }
        return Result.failure(NullPointerException())
    }
}