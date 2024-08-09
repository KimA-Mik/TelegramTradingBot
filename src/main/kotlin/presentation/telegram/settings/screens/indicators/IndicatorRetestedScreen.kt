package presentation.telegram.settings.screens.indicators

import com.github.kotlintelegrambot.entities.ParseMode
import com.github.kotlintelegrambot.entities.ReplyMarkup
import presentation.telegram.screens.BotScreen
import presentation.telegram.settings.IndicatorType

class IndicatorRetestedScreen(id: Long, newValue: Boolean, indicatorType: IndicatorType) : BotScreen(id) {
    override val text: String
        get() = TODO("Not yet implemented")
    override val replyMarkup: ReplyMarkup
        get() = TODO("Not yet implemented")
    override val parseMode: ParseMode
        get() = TODO("Not yet implemented")
}