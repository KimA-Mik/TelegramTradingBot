package presentation.telegram.screens

import com.github.kotlintelegrambot.entities.ParseMode
import domain.updateService.model.NotifyShare
import presentation.common.mappers.toInsufficientUpdateText

class FuturePriceInsufficientUpdate(
    userId: Long,
    messageId: Long? = null,
    val share: NotifyShare
) : BotScreen(userId, messageId) {
    override val text = share.toInsufficientUpdateText()
    override val replyMarkup = null
    override val parseMode = ParseMode.MARKDOWN
    override val disableWebPagePreview = true
}