package presentation.telegram.security.update

import com.github.kotlintelegrambot.entities.InlineKeyboardMarkup
import com.github.kotlintelegrambot.entities.keyboard.InlineKeyboardButton
import domain.common.formatToRu
import domain.techanalysis.BollingerBands
import domain.updateservice.indicators.CacheEntry
import domain.user.model.SecurityType
import domain.user.model.TrackingSecurity
import domain.util.MathUtil
import kotlinx.datetime.TimeZone
import kotlinx.datetime.format
import kotlinx.datetime.toLocalDateTime
import presentation.telegram.security.list.callbackbutton.EditSecurityCallbackButton
import presentation.util.PresentationUtil
import presentation.util.TelegramUtil
import presentation.util.TinInvestUtil
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

const val UPDATE_BUILDER_CAPACITY = 1024

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

fun StringBuilder.appendIndicatorsToSecurityAlert(
    indicators: CacheEntry?, currentPrice: Double,
    //TODO: Have fun with mfi
    renderMFI: Boolean = false,
    rsiLow: Double = MathUtil.RSI_LOW, rsiHigh: Double = MathUtil.RSI_HIGH,
    bbLow: Double = MathUtil.BB_CRITICAL_LOW, bbHigh: Double = MathUtil.BB_CRITICAL_HIGH
) {
    if (indicators == null) return

    appendLine("*Индикаторы:*")
    var color = PresentationUtil.rsiColor(indicators.min15Rsi, rsiLow, rsiHigh)
    append('*', color, "RSI (15м):* ", indicators.min15Rsi.formatToRu(), '\n')
    color = PresentationUtil.rsiColor(indicators.hourlyRsi, rsiLow, rsiHigh)
    append('*', color, "RSI (1ч):* ", indicators.hourlyRsi.formatToRu(), '\n')
    color = PresentationUtil.rsiColor(indicators.hour4Rsi, rsiLow, rsiHigh)
    append('*', color, "RSI (4ч):* ", indicators.hour4Rsi.formatToRu(), '\n')
    color = PresentationUtil.rsiColor(indicators.dailyRsi, rsiLow, rsiHigh)
    append('*', color, "RSI (1д):* ", indicators.dailyRsi.formatToRu(), '\n')

    if (renderMFI) {
        color = PresentationUtil.rsiColor(indicators.min15Mfi)
        append('*', color, "MFI (15м):* ", indicators.min15Mfi.formatToRu(), '\n')
        color = PresentationUtil.rsiColor(indicators.hourlyMfi)
        append('*', color, "MFI (1ч):* ", indicators.hourlyMfi.formatToRu(), '\n')
        color = PresentationUtil.rsiColor(indicators.hour4Mfi)
        append('*', color, "MFI (4ч):* ", indicators.hour4Mfi.formatToRu(), '\n')
        color = PresentationUtil.rsiColor(indicators.dailyMfi)
        append('*', color, "MFI (1д):* ", indicators.dailyMfi.formatToRu(), '\n')
    }

    renderBb(indicators.min15bb, currentPrice, "15м", bbLow, bbHigh)
    renderBb(indicators.hourlyBb, currentPrice, "1ч", bbLow, bbHigh)
    renderBb(indicators.hour4Bb, currentPrice, "4ч", bbLow, bbHigh)
    renderBb(indicators.dailyBb, currentPrice, "1д", bbLow, bbHigh)
}

fun StringBuilder.appendPlannedPricesToSecurityAlert(security: TrackingSecurity) {
    append("*Планируемая цена покупки:* ", PresentationUtil.formatTargetPrice(security.lowTargetPrice), '\n')
    append("*Планируемая цена продажи:* ", PresentationUtil.formatTargetPrice(security.targetPrice), '\n')
}

fun StringBuilder.renderBb(
    bollingerBandsData: BollingerBands.BollingerBandsData,
    currentPrice: Double,
    intervalsString: String,
    lowPercent: Double = MathUtil.BB_CRITICAL_LOW,
    highPercent: Double = MathUtil.BB_CRITICAL_HIGH,
) {
    val bbColor = PresentationUtil.markupBbColor(
        currentPrice,
        bollingerBandsData.lower,
        bollingerBandsData.upper,
        lowPercent,
        highPercent
    )
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