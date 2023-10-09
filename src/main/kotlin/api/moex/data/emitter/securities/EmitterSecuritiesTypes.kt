package api.moex.data.emitter.securities

enum class EmitterSecuritiesTypes(val type: String) {
    COMMON_SHARE("common_share"), //Акция обыкновенная
    FUTURES("futures") //Фьючерс
}