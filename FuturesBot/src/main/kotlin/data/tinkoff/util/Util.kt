package data.tinkoff.util

import com.google.protobuf.Timestamp
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

@OptIn(ExperimentalTime::class)
fun Instant.toTimestamp(): Timestamp = Timestamp.newBuilder()
    .setSeconds(epochSeconds)
    .setNanos(nanosecondsOfSecond)
    .build()