package presentation.telegram.updates.indicatorUpdate

import com.github.kotlintelegrambot.entities.ParseMode
import domain.updateService.updates.IndicatorUpdateData
import presentation.common.MarkdownUtil
import presentation.common.TinInvestUtil
import presentation.common.formatAndTrim
import presentation.common.mappers.IndicatorUpdateMapper
import presentation.telegram.common.ROUBLE_SIGN
import presentation.telegram.screens.BotScreen

class IndicatorsUpdateScreen(
    userId: Long,
    messageId: Long? = null,
    ticker: String,
    price: Double,
    updateData: List<IndicatorUpdateData>
) : BotScreen(userId, messageId) {
    override val text: String
    override val replyMarkup = null
    override val parseMode = ParseMode.MARKDOWN
    override val disableWebPagePreview = true

    init {
        val tickerUrl = TinInvestUtil.shareUrl(ticker)
        var toText = "Обновление индикаторов ${MarkdownUtil.inlineUrl(text = ticker, url = tickerUrl)} "
        toText += "(${price.formatAndTrim(2)}$ROUBLE_SIGN)\n"
        updateData.forEach {
            toText += IndicatorUpdateMapper.convertToText(it)
        }
        text = toText
    }
}