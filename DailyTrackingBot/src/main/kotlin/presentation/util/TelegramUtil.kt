package presentation.util

import domain.user.model.TrackingSecurity
import ru.kima.telegrambot.common.util.MarkdownUtil
import ru.kima.telegrambot.common.util.TelegramUtil
import ru.kima.telegrambot.common.util.TinInvestUtil

fun TelegramUtil.clickableTrackingSecurity(security: TrackingSecurity) = MarkdownUtil.inlineUrl(
    text = security.ticker,
    url = TinInvestUtil.securityUrl(security)
)
