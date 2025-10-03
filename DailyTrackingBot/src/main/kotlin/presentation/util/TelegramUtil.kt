package presentation.util

import ru.kima.cacheserver.api.schema.model.Future
import ru.kima.cacheserver.api.schema.model.Security
import ru.kima.cacheserver.api.schema.model.Share

object TelegramUtil {
    fun copiableText(text: String) = "`$text`"

    fun clickableSecurity(security: Security) = MarkdownUtil.inlineUrl(
        text = security.ticker,
        url = when (security) {
            is Future -> TinInvestUtil.futureUrl(security.ticker)
            is Share -> TinInvestUtil.shareUrl(security.ticker)
        }
    )
}
