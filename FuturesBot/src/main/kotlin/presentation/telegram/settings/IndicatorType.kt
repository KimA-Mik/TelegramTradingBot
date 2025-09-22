package presentation.telegram.settings

import domain.common.Indicator

enum class IndicatorType(val text: String, val shortName: String) {
    RSI("RSI", "RSI") {
        override fun toIndicator() = Indicator.RSI

    },
    BOLLINGER_BANDS("Полосы Боллинджера", "ПБ") {
        override fun toIndicator() = Indicator.BOLLINGER_BANDS
    };

    abstract fun toIndicator(): Indicator

    companion object {
        fun fromName(name: String): IndicatorType? {
            return entries.find { it.name.equals(name, true) }
        }
    }
}