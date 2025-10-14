package presentation.telegram.security.edit.screen

import com.github.kotlintelegrambot.entities.ParseMode
import domain.common.ROUBLE_SIGN
import domain.common.formatToRu
import domain.user.usecase.UpdateExpectedPriceUseCase
import presentation.telegram.core.screen.BotScreen

class EditPriceResultScreen(
    userId: Long, price: Double?,
    priceType: UpdateExpectedPriceUseCase.PriceType
) : BotScreen(userId) {
    override val text: String =
        if (price == null) "Не удалось обновить цену" else when (priceType) {
            UpdateExpectedPriceUseCase.PriceType.HIGH -> "Новая цена продажи: ${price.formatToRu()}$ROUBLE_SIGN"
            UpdateExpectedPriceUseCase.PriceType.LOW -> "Новая цена покупки: ${price.formatToRu()}$ROUBLE_SIGN"
        }
    override val parseMode = ParseMode.MARKDOWN
}