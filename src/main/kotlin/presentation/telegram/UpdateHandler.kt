package presentation.telegram

import domain.updateService.UpdateService
import domain.updateService.updates.Update
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.map
import presentation.telegram.screens.BotScreen

class UpdateHandler(
    service: UpdateService
) {
    val outScreens = service
        .updates
        .map {
            println(it)
            updateToScreen(it)
        }
        .filterNotNull()

    private fun updateToScreen(update: Update): BotScreen? {
        return null
    }

}