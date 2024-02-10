package domain.moex.securities.model

data class Security(
    val secId: String = String(),
    val shortName: String = String(),
    val name: String = String(),
    val market: String = String(),
    val engine: String = String(),
    val price: Double = 0.0,
    val emitter: Int = 0,
    val futures: List<Futures> = emptyList(),
    val time: String = String()
)
