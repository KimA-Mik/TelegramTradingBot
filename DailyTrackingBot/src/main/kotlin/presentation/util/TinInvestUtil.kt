package presentation.util

import domain.user.model.SecurityType
import domain.user.model.TrackingSecurity
import ru.kima.cacheserver.api.schema.model.Future
import ru.kima.cacheserver.api.schema.model.Security
import ru.kima.cacheserver.api.schema.model.Share

object TinInvestUtil {
    private const val URL = "https://www.tinkoff.ru/invest/"

    fun shareUrl(ticker: String) = URL + "stocks/$ticker"
    fun futureUrl(ticker: String) = URL + "futures/$ticker"

    fun securityUrl(security: TrackingSecurity) = when (security.type) {
        SecurityType.SHARE -> shareUrl(security.ticker)
        SecurityType.FUTURE -> futureUrl(security.ticker)
    }

    fun securityUrl(security: Security) = when (security) {
        is Future -> futureUrl(security.ticker)
        is Share -> shareUrl(security.ticker)
    }
}