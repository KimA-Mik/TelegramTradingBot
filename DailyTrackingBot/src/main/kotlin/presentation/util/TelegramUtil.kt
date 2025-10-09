package presentation.util

import domain.user.model.SecurityType
import domain.user.model.TrackingSecurity
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

    fun clickableTrackingSecurity(security: TrackingSecurity) = MarkdownUtil.inlineUrl(
        text = security.ticker,
        url = when (security.type) {
            SecurityType.SHARE -> TinInvestUtil.shareUrl(security.ticker)
            SecurityType.FUTURE -> TinInvestUtil.futureUrl(security.ticker)
        }
    )
}
