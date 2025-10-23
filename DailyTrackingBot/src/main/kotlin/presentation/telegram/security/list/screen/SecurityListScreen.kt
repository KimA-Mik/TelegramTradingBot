package presentation.telegram.security.list.screen

import com.github.kotlintelegrambot.entities.InlineKeyboardMarkup
import com.github.kotlintelegrambot.entities.ParseMode
import com.github.kotlintelegrambot.entities.ReplyMarkup
import domain.common.ROUBLE_SIGN
import domain.common.formatToRu
import domain.user.model.SecurityType
import domain.user.model.TrackingSecurity
import presentation.telegram.core.screen.BotScreen
import presentation.telegram.security.list.callbackbutton.EditSecurityCallbackButton
import presentation.telegram.security.list.callbackbutton.SecuritiesListBackwardCallbackButton
import presentation.telegram.security.list.callbackbutton.SecuritiesListForwardCallbackButton
import presentation.util.MarkdownUtil
import presentation.util.TinInvestUtil

class SecurityListScreen(
    userId: Long,
    messageId: Long? = null,
    private val securities: List<TrackingSecurity>,
    private val page: Int = 0,
    private val pageSize: Int = 0,
    private val totalPages: Int = 0
) : BotScreen(userId, messageId) {
    override val text = renderText()
    override val parseMode = ParseMode.MARKDOWN
    override val disableWebPagePreview = true
    override val replyMarkup = calculateReplayMarkup()

    private fun renderText() = buildString {
        if (securities.isEmpty()) {
            appendLine("У вас нет отслеживаемых бумаг.")
            return@buildString
        }

        securities.forEachIndexed { index, security ->
            val securityIndex = pageSize * (page - 1) + index + 1
            val inlineSecurityUrl = MarkdownUtil.inlineUrl(
                text = security.ticker,
                url = when (security.type) {
                    SecurityType.SHARE -> TinInvestUtil.shareUrl(security.ticker)
                    SecurityType.FUTURE -> TinInvestUtil.futureUrl(security.ticker)
                }
            )

            append(securityIndex, ". ", inlineSecurityUrl, ": ", security.name, " — ")

            append(
                "🟩 ", //Green
                security.lowTargetPrice.formatToRu(), ROUBLE_SIGN,
                " - 🟥 ", //Red
                security.targetPrice.formatToRu(), ROUBLE_SIGN
            )
            append(" (±", security.targetDeviation.formatToRu(), "%)")

            append(' ')
            append(if (security.isActive) "✅" else "❌", "\n")
            appendLine()
        }


        if (totalPages > 1) {
            appendLine()
            append("Страница ", page, " из ", totalPages)
        }
    }

    private fun calculateReplayMarkup(): ReplyMarkup? {
        if (securities.isEmpty()) return null
        val buttons = buildList {
            if (totalPages > 1) {
                add(
                    listOf(
                        SecuritiesListBackwardCallbackButton.getCallbackData(page),
                        SecuritiesListForwardCallbackButton.getCallbackData(page)
                    )
                )
            }

            securities.forEach {
                add(
                    listOf(EditSecurityCallbackButton.getCallbackData(it.ticker))
                )
            }
        }

        return InlineKeyboardMarkup.create(buttons)
    }
}