package api.moex.data.security

data class SecurityInfo(
    val security: SecurityEntry,
    val marketData: MarketDataEntry
)