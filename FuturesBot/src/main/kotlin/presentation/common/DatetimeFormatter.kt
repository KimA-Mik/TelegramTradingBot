package presentation.common

import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.format.Padding
import kotlinx.datetime.format.char


val priceDateFormat = LocalDateTime.Format {
    day(padding = Padding.ZERO)
    char('/')
    monthNumber()

    char(' ')

    hour()
    char(':')
    minute()
    char(':')
    second()
}

val futureDateFormat = LocalDateTime.Format {
    day(padding = Padding.ZERO)
    char('.')
    monthNumber()
    char('.')
    yearTwoDigits(2000)
}