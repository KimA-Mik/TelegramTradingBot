package domain.tinkoff.model

data class TinkoffSecurity(
    val share: TinkoffShare = TinkoffShare(),
    val futures: List<TinkoffFuture> = emptyList()
)