package presentation.telegram

import domain.updateService.UpdateService
import domain.updateService.updates.telegramUpdates.IndicatorUpdate
import domain.updateService.updates.telegramUpdates.SharePriceInsufficientUpdate
import domain.updateService.updates.telegramUpdates.ShareUpdate
import domain.updateService.updates.telegramUpdates.Update
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

    private fun updateToScreen(update: Update): BotScreen {
        return when (update) {
            is ShareUpdate -> FuturePriceUpdate(
                userId = update.userId,
                state = FuturePriceUpdate.State.ShowUpdate(
                    share = update.share
                )
            )

            is SharePriceInsufficientUpdate -> FuturePriceInsufficientUpdate(
                userId = update.userId,
                share = update.share
            )

            is IndicatorUpdate -> IndicatorsUpdateScreen(
                userId = update.userId,
                ticker = update.ticker,
                price = update.price,
                updateData = update.data
            )
        }
    }
}