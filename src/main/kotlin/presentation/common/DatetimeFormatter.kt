package presentation.common

import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.format.char


val priceDateFormat = LocalDateTime.Format {
    dayOfMonth()
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
    dayOfMonth()
    char('.')
    monthNumber()
    char('.')
    yearTwoDigits(2000)
}