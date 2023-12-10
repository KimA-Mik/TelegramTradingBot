package domain.securities.model

data class Security(
    val secId: String,
    val shortName: String,
    val name: String,
    val market: String,
    val engine: String,
    val price: Double,
    val futures: List<String> = emptyList()
)
