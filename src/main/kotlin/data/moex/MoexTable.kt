package data.moex

data class MoexTable(
    val name: String,
    val data: MutableList<MutableMap<String, Any>> = mutableListOf(),
    val fields: MutableList<String> = mutableListOf()
)