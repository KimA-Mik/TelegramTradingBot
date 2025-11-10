package presentation.telegram

import domain.updateservice.UpdateService
import kotlinx.coroutines.flow.mapNotNull

class UpdateHandler(
    service: UpdateService
) {
    val outScreens = service
        .updates
        .mapNotNull {
            null
        }

}