package presentation.telegram

import domain.updateService.UpdateService
import domain.updateService.updates.Update
import kotlinx.coroutines.flow.map
import presentation.telegram.screens.BotScreen
import presentation.telegram.screens.ErrorScreen

class UpdateHandler(
    service: UpdateService
) {
    val outScreens = service
        .updates
        .map {
            updateToScreen(it)
        }

    private fun updateToScreen(update: Update): BotScreen {
        return ErrorScreen(update.userId, "")
    }

}