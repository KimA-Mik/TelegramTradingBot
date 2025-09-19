package presentation.common.mappers

import domain.updateService.model.NotifyShare
import domain.utils.DateUtil
import kotlinx.datetime.format
import kotlinx.datetime.toLocalDateTime
import presentation.common.MarkdownUtil
import presentation.common.TinInvestUtil
import presentation.common.formatAndTrim
import presentation.common.futureDateFormat
import presentation.telegram.common.ROUBLE_SIGN
import kotlin.time.ExperimentalTime


@OptIn(ExperimentalTime::class)
fun NotifyShare.toUpdateText(): String {
    var res = String()
    val inlineShareUrl = MarkdownUtil.inlineUrl(
        text = shareTicker,
        url = TinInvestUtil.shareUrl(shareTicker)
    )
    res += "$inlineShareUrl: ${sharePrice.formatAndTrim(2)}$ROUBLE_SIGN"

    futures.forEach { future ->
        val date = future.expirationDate.toLocalDateTime(DateUtil.timezoneMoscow).format(futureDateFormat)
        val inlineFutureUrl = MarkdownUtil.inlineUrl(
            text = future.ticker,
            url = TinInvestUtil.futureUrl(future.ticker)
        )
        res += "\n$date - $inlineFutureUrl: ${future.price.formatAndTrim(2)}$ROUBLE_SIGN, " +
                "${future.annualPercent.formatAndTrim(2)}% годовых, " +
                "после налога ${future.annualAfterTaxes.formatAndTrim(2)}%"
    }

    return res
}

fun NotifyShare.toInsufficientUpdateText(): String {
    val shareInlineUrl = MarkdownUtil.inlineUrl(
        text = shareTicker,
        url = TinInvestUtil.shareUrl(shareTicker)
    )
    return "Для $shareInlineUrl процент годовых стал меньше, чем ${minimalDifference.formatAndTrim(2)}%"
}