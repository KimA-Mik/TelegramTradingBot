package presentation.telegram.screens

import com.github.kotlintelegrambot.entities.InlineKeyboardMarkup
import com.github.kotlintelegrambot.entities.ParseMode
import com.github.kotlintelegrambot.entities.ReplyMarkup
import com.github.kotlintelegrambot.entities.keyboard.InlineKeyboardButton
import domain.user.model.UserShare
import presentation.common.TelegramUtil
import presentation.common.TinInvestUtil
import presentation.common.formatAndTrim
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
    override val parseMode = ParseMode.MARKDOWN
    override val disableWebPagePreview = true

    init {
        text = markupText()
        replyMarkup = calculateReplayMarkup()
    }

    private fun markupText(): String {
        if (shares.isEmpty()) return "У вас нет отслеживаемых акций"

        var result = "($page/$totalPages)"

        shares.forEachIndexed { index, share ->
            val shareIndex = pageSize * (page - 1) + index + 1
            val inlineShareUrl = TelegramUtil.markdownInlineUrl(
                text = share.ticker,
                url = TinInvestUtil.shareUrl(share.ticker)
            )
            result += "\n$shareIndex. $inlineShareUrl: ${share.name} (${share.percent.formatAndTrim(2)}%)"
        }

        return result
    }

    private fun calculateReplayMarkup(): ReplyMarkup? {
        if (shares.isEmpty()) return null

        val buttonsList = mutableListOf(
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
            )
        )

        val sharesRows = shares.map { share ->
            listOf(
                InlineKeyboardButton.CallbackData(
                    CallbackButton.EditShare.text + share.ticker,
                    CallbackButton.EditShare.callbackData +
                            CALLBACK_BUTTON_ARGUMENT_SEPARATOR
                            + share.ticker
                )
            )
        }

        buttonsList.addAll(sharesRows)
        return InlineKeyboardMarkup.create(buttonsList)
    }
}