package presentation.agent.updates

import domain.updateService.updates.IndicatorUpdateData
import presentation.agent.AGENT_MARKDOWN_V2
import presentation.agent.AgentScreen
import presentation.common.MarkdownUtil
import presentation.common.TinInvestUtil
import presentation.common.formatAndTrim
import presentation.common.mappers.IndicatorUpdateMapper
import presentation.telegram.common.ROUBLE_SIGN

class AgentIndicatorUpdateScreen(
    chatId: String,
    ticker: String,
    price: Double,
    updateData: List<IndicatorUpdateData>
) : AgentScreen(chatId) {
    override val text: String
    override val parseMode = AGENT_MARKDOWN_V2

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