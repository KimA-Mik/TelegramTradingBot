package presentation.telegram.common

import java.util.*

fun Double.format(length: Int): String {
    return String.format(
        Locale.US,
        "%.${length}f",
        this
    )
}

fun Double.formatAndTrim(length: Int): String {
    return String.format(
        Locale.US,
        "%.${length}f",
        this
    ).trimEnd('0', '.')
}