package ru.kima.cacheserver.implementation.data.util

import com.google.protobuf.Timestamp
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

@OptIn(ExperimentalTime::class)
fun Instant.toTimestamp(): Timestamp = Timestamp.newBuilder()
    .setSeconds(epochSeconds)
    .setNanos(nanosecondsOfSecond)
    .build()

@OptIn(ExperimentalTime::class)
fun Timestamp.toInstant() = Instant.fromEpochSeconds(
    epochSeconds = seconds,
    nanosecondAdjustment = nanos
)