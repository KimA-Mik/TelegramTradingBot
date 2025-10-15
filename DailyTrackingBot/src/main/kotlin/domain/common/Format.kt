package domain.common

import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.*

private val df = DecimalFormat(
    "#,##0.####",
    DecimalFormatSymbols(Locale.Builder().setLanguage("ru").setRegion("RU").build())
)

fun Double.formatToRu(): String = df.format(this)

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