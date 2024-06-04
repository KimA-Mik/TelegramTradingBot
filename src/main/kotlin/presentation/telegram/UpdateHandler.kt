package presentation.telegram

import domain.updateService.UpdateService
import domain.updateService.updates.telegramUpdates.TelegramIndicatorUpdate
import domain.updateService.updates.telegramUpdates.TelegramSharePriceInsufficientUpdate
import domain.updateService.updates.telegramUpdates.TelegramShareUpdate
import domain.updateService.updates.telegramUpdates.TelegramUpdate
import kotlinx.coroutines.flow.mapNotNull
import presentation.telegram.screens.BotScreen
import presentation.telegram.screens.FuturePriceInsufficientUpdate
import presentation.telegram.screens.FuturePriceUpdate
import presentation.telegram.updates.indicatorUpdate.IndicatorsUpdateScreen

class UpdateHandler(
    service: UpdateService
) {
    val outScreens = service
        .updates
        .mapNotNull {
            updateToScreen(it)
        }

    private fun updateToScreen(update: TelegramUpdate): BotScreen {
        return when (update) {
            is TelegramShareUpdate -> FuturePriceUpdate(
                userId = update.userId,
                state = FuturePriceUpdate.State.ShowUpdate(
                    share = update.share
                )
            )

            is TelegramSharePriceInsufficientUpdate -> FuturePriceInsufficientUpdate(
                userId = update.userId,
                share = update.share
            )

            is TelegramIndicatorUpdate -> IndicatorsUpdateScreen(
                userId = update.userId,
                ticker = update.ticker,
                price = update.price,
                updateData = update.data
            )
        }
    }
}