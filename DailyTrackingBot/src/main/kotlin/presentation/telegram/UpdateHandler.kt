package presentation.telegram

import domain.updateservice.TelegramUpdate
import domain.updateservice.UpdateService
import kotlinx.coroutines.flow.mapNotNull
import presentation.telegram.core.screen.BotScreen
import presentation.telegram.security.update.PriceAlertScreen
import presentation.telegram.security.update.RsiAlertScreen

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
            is TelegramUpdate.PriceAlert -> PriceAlertScreen(update)
            is TelegramUpdate.RsiAlert -> RsiAlertScreen(update)
        }
    }
}