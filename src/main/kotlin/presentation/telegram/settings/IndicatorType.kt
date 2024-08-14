package presentation.telegram.settings

enum class IndicatorType(val text: String, val shortName: String) {
    RSI("RSI", "RSI"),
    BOLLINGER_BANDS("Полосы Боллинджера", "ПБ");

    companion object {
        fun fromName(name: String): IndicatorType? {
            return entries.find { it.name.equals(name, true) }
        }
    }
}