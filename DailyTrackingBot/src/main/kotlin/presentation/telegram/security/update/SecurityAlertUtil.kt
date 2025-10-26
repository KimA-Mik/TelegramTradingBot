package presentation.telegram.security.update

import com.github.kotlintelegrambot.entities.InlineKeyboardMarkup
import com.github.kotlintelegrambot.entities.keyboard.InlineKeyboardButton
import domain.common.formatToRu
import domain.techanalysis.BollingerBands
import domain.updateservice.indicators.CacheEntry
import domain.user.model.SecurityType
import domain.user.model.TrackingSecurity
import kotlinx.datetime.TimeZone
import kotlinx.datetime.format
import kotlinx.datetime.toLocalDateTime
import presentation.telegram.security.list.callbackbutton.EditSecurityCallbackButton
import presentation.util.PresentationUtil
import presentation.util.TelegramUtil
import presentation.util.TinInvestUtil
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

fun StringBuilder.renderSecurityTitleForAlert(security: TrackingSecurity) {
    val type = when (security.type) {
        SecurityType.FUTURE -> "*Фьючерс:* "
        SecurityType.SHARE -> "*Акция:* "
    }

    append(type)
    append(TelegramUtil.hashtag(security.ticker))
    append(" — (")
    append(security.name)
    appendLine(')')
}

@OptIn(ExperimentalTime::class)
fun StringBuilder.appendNoteToSecurityAlert(security: TrackingSecurity) {
    security.note?.takeIf { it.isNotBlank() }?.let {
        append("Заметка")
        security.noteUpdatedMs?.let {
            val ldt = Instant.fromEpochMilliseconds(it).toLocalDateTime(TimeZone.currentSystemDefault())
            append(" (")
            append(ldt.format(TelegramUtil.localDateTimeFormat))
            append(')')
        }
        appendLine(":")
        append(security.note)
    }
}

fun StringBuilder.appendIndicatorsToSecurityAlert(indicators: CacheEntry?, currentPrice: Double) {
    if (indicators == null) return

    appendLine("*Индикаторы:*")
    appendLine("*${PresentationUtil.rsiColor(indicators.min15Rsi)}RSI (15м):* ${indicators.min15Rsi.formatToRu()}")
    appendLine("*${PresentationUtil.rsiColor(indicators.hourlyRsi)}RSI (1ч):* ${indicators.hourlyRsi.formatToRu()}")
    appendLine("*${PresentationUtil.rsiColor(indicators.hour4Rsi)}RSI (4ч):* ${indicators.hour4Rsi.formatToRu()}")
    appendLine("*${PresentationUtil.rsiColor(indicators.dailyRsi)}RSI (1д):* ${indicators.dailyRsi.formatToRu()}")

    renderBb(indicators.min15bb, currentPrice, "15м")
    renderBb(indicators.hourlyBb, currentPrice, "1ч")
    renderBb(indicators.hour4Bb, currentPrice, "4ч")
    renderBb(indicators.dailyBb, currentPrice, "1д")
//    var bbColor = PresentationUtil.markupBbColor(currentPrice, indicators.min15bb.lower, indicators.min15bb.upper)
//    append("*${bbColor}BB (15м):* ${indicators.min15bb.lower.formatToRu()} - ")
//    append("*${indicators.min15bb.middle.formatToRu()}* - ")
//    appendLine(indicators.min15bb.upper.formatToRu())
//    bbColor = PresentationUtil.markupBbColor(currentPrice, indicators.hourlyBb.lower, indicators.hourlyBb.upper)
//    append("*${bbColor}BB (1ч):* ${indicators.hourlyBb.lower.formatToRu()} - ")
//    append("*${indicators.hourlyBb.middle.formatToRu()}* - ")
//    appendLine(indicators.hourlyBb.upper.formatToRu())
//    bbColor = PresentationUtil.markupBbColor(currentPrice, indicators.hour4Bb.lower, indicators.hour4Bb.upper)
//    append("*${bbColor}BB (4ч):* ${indicators.hour4Bb.lower.formatToRu()} - ")
//    append("*${indicators.hour4Bb.middle.formatToRu()}* - ")
//    appendLine(indicators.hour4Bb.upper.formatToRu())
//    bbColor = PresentationUtil.markupBbColor(currentPrice, indicators.dailyBb.lower, indicators.dailyBb.upper)
//    append("*${bbColor}BB (1д):* ${indicators.dailyBb.lower.formatToRu()} - ")
//    append("*${indicators.dailyBb.middle.formatToRu()}* - ")
//    appendLine(indicators.dailyBb.upper.formatToRu())
}

fun StringBuilder.renderBb(
    bollingerBandsData: BollingerBands.BollingerBandsData,
    currentPrice: Double,
    intervalsString: String
) {
    val bbColor = PresentationUtil.markupBbColor(currentPrice, bollingerBandsData.lower, bollingerBandsData.upper)
    append('*', bbColor, "BB (", intervalsString, "):* ", bollingerBandsData.lower.formatToRu(), " - ")
    append('*', bollingerBandsData.middle.formatToRu(), "* - ")
    appendLine(bollingerBandsData.upper.formatToRu())
}

fun defaultSecurityAlertReplayMarkup(security: TrackingSecurity) = InlineKeyboardMarkup.create(
    listOf(
        listOf(
            InlineKeyboardButton.Url(
                text = PresentationUtil.T_INVEST_TITLE,
                url = TinInvestUtil.securityUrl(security)
            )
        ),
        listOf(EditSecurityCallbackButton.getCallbackData(security.ticker))
    )
)