package presentation.telegram

import domain.updateService.UpdateService
import domain.updateService.updates.ShareUpdate
import domain.updateService.updates.Update
import kotlinx.coroutines.flow.mapNotNull
import presentation.telegram.screens.BotScreen
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
        }
    }
}