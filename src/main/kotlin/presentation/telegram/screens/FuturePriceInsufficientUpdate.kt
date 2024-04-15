package presentation.telegram.screens

import domain.updateService.model.NotifyShare
import presentation.telegram.common.formatAndTrim

class FuturePriceInsufficientUpdate(
    userId: Long,
    messageId: Long? = null,
    val share: NotifyShare
) : BotScreen(userId, messageId) {
    override val replyMarkup = null
    override val parseMode = null
    override val text = "Для ${share.shareTicker} разница меньше ${share.minimalDifference.formatAndTrim(2)}%"
}