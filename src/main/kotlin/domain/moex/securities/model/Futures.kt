package domain.moex.securities.model

data class Futures(
    val secId: String = String(),
    val shortName: String = String(),
    val name: String = String(),
    val price: Double = 0.0,
    val lotSize: Int? = 0,
    val time: String = String()
)
