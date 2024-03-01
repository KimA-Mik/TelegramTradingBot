package domain.tinkoff.model

data class FullTinkoffSecurity(
    val security: TinkoffSecurity,
    val sharePrice: TinkoffPrice,
    val futuresPrices: List<TinkoffPrice>
)
