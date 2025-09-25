package domain.tinkoff.model

data class TinkoffShare(
    val uid: String = String(),
    val ticker: String = String(),
    val name: String = String(),
    val lot: Int = 0
)
