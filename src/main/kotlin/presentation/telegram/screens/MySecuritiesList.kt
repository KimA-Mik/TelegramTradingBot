package presentation.telegram.screens

import com.github.kotlintelegrambot.entities.InlineKeyboardMarkup
import com.github.kotlintelegrambot.entities.ReplyMarkup
import com.github.kotlintelegrambot.entities.keyboard.InlineKeyboardButton
import domain.user.model.UserShare
import presentation.telegram.callbackButtons.CALLBACK_BUTTON_ARGUMENT_SEPARATOR
import presentation.telegram.callbackButtons.CallbackButton

class MySecuritiesList(
    id: Long,
    messageId: Long? = null,
    private val shares: List<UserShare> = emptyList(),
    private val page: Int = 0,
    private val pageSize: Int = 0,
    private val totalPages: Int = 0
) : BotScreen(id, messageId) {
    override val text: String
    override val replyMarkup: ReplyMarkup?
    override val parseMode = null

    init {
        text = markupText()
        replyMarkup = calculateReplayMarkup()
    }

    private fun markupText(): String {
        if (shares.isEmpty()) return "У вас нет отследиваемых акций"

        var result = "($page/$totalPages)"

        shares.forEachIndexed { index, share ->
            val shareIndex = pageSize * (page - 1) + index + 1
            result += "\n$shareIndex. ${share.ticker}: ${share.name} (${share.percent}%)"
        }

        return result
    }

    private fun calculateReplayMarkup(): ReplyMarkup? {
        if (shares.isEmpty()) return null

        return InlineKeyboardMarkup.create(
            listOf(
                InlineKeyboardButton.CallbackData(
                    CallbackButton.SecuritiesListBack.text,
                    CallbackButton.SecuritiesListBack.callbackData +
                            CALLBACK_BUTTON_ARGUMENT_SEPARATOR
                            + page.toString()
                ),
                InlineKeyboardButton.CallbackData(
                    CallbackButton.SecuritiesListForward.text,
                    CallbackButton.SecuritiesListForward.callbackData +
                            CALLBACK_BUTTON_ARGUMENT_SEPARATOR
                            + page.toString()
                )
            ),
        )
    }
}