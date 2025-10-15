package presentation.telegram.security.edit.screen

import domain.user.usecase.UpdateExpectedPriceUseCase
import presentation.telegram.core.screen.BotScreen

class EditPriceScreen(
    userId: Long,
    priceType: UpdateExpectedPriceUseCase.PriceType
) : BotScreen(userId) {
    override val text = when (priceType) {
        UpdateExpectedPriceUseCase.PriceType.HIGH -> "Введите цену продажи"
        UpdateExpectedPriceUseCase.PriceType.LOW -> "Введите цену покупки"
    }
    override val replyMarkup = basicReplayMarkup
}