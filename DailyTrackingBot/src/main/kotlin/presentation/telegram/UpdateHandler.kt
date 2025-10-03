package presentation.telegram

import domain.updateservice.TelegramUpdate
import domain.updateservice.UpdateService
import kotlinx.coroutines.flow.mapNotNull
import presentation.telegram.core.screen.BotScreen
import presentation.telegram.security.screen.PriceAlertScreen

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
        }
    }
}