package presentation.telegram.model

import domain.tinkoff.model.TinkoffPrice
import domain.tinkoff.model.TinkoffSecurity

data class SecuritySearchResultData(
    val security: TinkoffSecurity,
    val sharePrice: TinkoffPrice,
    val futuresPrices: List<TinkoffPrice>
)
