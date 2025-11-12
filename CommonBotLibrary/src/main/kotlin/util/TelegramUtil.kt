package ru.kima.telegrambot.common.util

import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.format.DateTimeComponents
import kotlinx.datetime.format.DateTimeFormatBuilder
import kotlinx.datetime.format.char
import ru.kima.cacheserver.api.schema.model.Security

object TelegramUtil {
    fun hashtag(text: String) = "#${text.replace(" ", "_")}"
    fun copiableText(text: String) = "`$text`"
    fun clickableSecurity(security: Security) = MarkdownUtil.inlineUrl(
        text = security.ticker,
        url = TinInvestUtil.securityUrl(security)
    )

    val instantFormat = DateTimeComponents.Format { defaultFormat() }
    val localDateTimeFormat = LocalDateTime.Format { defaultFormat() }

    private fun DateTimeFormatBuilder.WithDateTime.defaultFormat() {
        day()
        char('.')
        monthNumber()
        char('.')
        year()
        char(' ')
        hour()
        char(':')
        minute()
    }
}
