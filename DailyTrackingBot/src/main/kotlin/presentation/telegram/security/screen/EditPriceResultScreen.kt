package presentation.telegram.security.screen

import com.github.kotlintelegrambot.entities.ParseMode
import domain.common.ROUBLE_SIGN
import domain.common.formatAndTrim
import presentation.telegram.core.screen.BotScreen

class EditPriceResultScreen(userId: Long, price: Double?) : BotScreen(userId) {
    override val text: String =
        if (price == null) "Не удалось обновить цену" else "Новая цена: *${price.formatAndTrim(2)}$ROUBLE_SIGN*"
    override val parseMode = ParseMode.MARKDOWN
}