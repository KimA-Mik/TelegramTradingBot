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
