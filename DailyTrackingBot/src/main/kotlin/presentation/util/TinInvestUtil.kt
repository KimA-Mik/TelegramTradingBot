package presentation.util

import domain.user.model.SecurityType
import domain.user.model.TrackingSecurity
import ru.kima.telegrambot.common.util.TinInvestUtil


fun TinInvestUtil.securityUrl(security: TrackingSecurity) = when (security.type) {
    SecurityType.SHARE -> shareUrl(security.ticker)
    SecurityType.FUTURE -> futureUrl(security.ticker)
}
