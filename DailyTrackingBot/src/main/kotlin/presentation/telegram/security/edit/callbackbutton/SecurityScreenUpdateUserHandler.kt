package presentation.telegram.security.edit.callbackbutton

import domain.tinkoff.usecase.FindSecurityUseCase
import domain.user.model.TrackingSecurity
import domain.user.model.User
import presentation.telegram.core.UiError
import presentation.telegram.core.screen.BotScreen
import presentation.telegram.core.screen.ErrorScreen
import presentation.telegram.security.edit.screen.SecurityScreen

class SecurityScreenUpdateUserHandler(
    private val findSecurity: FindSecurityUseCase
) {
    suspend fun handle(
        user: User,
        messageId: Long?,
        error: UiError = UiError.UnregisteredUserError,
        update: suspend () -> TrackingSecurity?
    ): BotScreen {
        val updated = update() ?: return ErrorScreen(user.id, error, messageId)

        return when (val security = findSecurity(user.id, updated.ticker)) {
            is FindSecurityUseCase.Result.Success -> SecurityScreen(
                user = user,
                security = updated,
                lastPrice = security.price,
                messageId = messageId
            )

            else -> SecurityScreen(
                user = user,
                security = null,
                lastPrice = null,
                messageId = messageId
            )
        }
    }
}