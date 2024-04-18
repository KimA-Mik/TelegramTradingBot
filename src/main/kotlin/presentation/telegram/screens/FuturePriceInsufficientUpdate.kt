package presentation.telegram.screens

import com.github.kotlintelegrambot.entities.ParseMode
import domain.updateService.model.NotifyShare
import presentation.common.TelegramUtil
import presentation.common.TinInvestUtil
import presentation.common.formatAndTrim

class FuturePriceInsufficientUpdate(
    userId: Long,
    messageId: Long? = null,
    val share: NotifyShare
) : BotScreen(userId, messageId) {
    override val text = generateText()
    override val replyMarkup = null
    override val parseMode = ParseMode.MARKDOWN
    override val disableWebPagePreview = true

    private fun generateText(): String {
        val shareInlineUrl = TelegramUtil.markdownInlineUrl(
            text = share.shareTicker,
            url = TinInvestUtil.shareUrl(share.shareTicker)
        )
        return "Для $shareInlineUrl процент годовых стал меньше, чем ${share.minimalDifference.formatAndTrim(2)}%"
    }
}