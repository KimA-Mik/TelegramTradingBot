package domain.common

import java.util.*

fun Double.format(
    length: Int,
    locale: Locale = Locale.US
): String = String.format(
    locale,
    "%.${length}f",
    this
)


fun Double.formatAndTrim(
    length: Int,
    locale: Locale = Locale.US
): String = String.format(
    locale,
    "%.${length}f",
    this
)
    .trimEnd('0')
    .trimEnd('.')
    .trimEnd(',')

fun String.parseToDouble() = replace(',', '.').toDouble()