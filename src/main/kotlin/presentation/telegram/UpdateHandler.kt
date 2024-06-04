package presentation.telegram

import domain.updateService.UpdateService
import domain.updateService.updates.telegramUpdates.IndicatorTelegramUpdate
import domain.updateService.updates.telegramUpdates.SharePriceInsufficientTelegramUpdate
import domain.updateService.updates.telegramUpdates.ShareTelegramUpdate
import domain.updateService.updates.telegramUpdates.TelegramUpdate
import kotlinx.coroutines.flow.mapNotNull
import presentation.telegram.indicatorUpdate.IndicatorsUpdateScreen
import presentation.telegram.screens.BotScreen
import presentation.telegram.screens.FuturePriceInsufficientUpdate
import presentation.telegram.screens.FuturePriceUpdate

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
            is ShareTelegramUpdate -> FuturePriceUpdate(
                userId = update.userId,
                state = FuturePriceUpdate.State.ShowUpdate(
                    share = update.share
                )
            )

            is SharePriceInsufficientTelegramUpdate -> FuturePriceInsufficientUpdate(
                userId = update.userId,
                share = update.share
            )

            is IndicatorTelegramUpdate -> IndicatorsUpdateScreen(
                userId = update.userId,
                ticker = update.ticker,
                price = update.price,
                updateData = update.data
            )
        }
    }
}