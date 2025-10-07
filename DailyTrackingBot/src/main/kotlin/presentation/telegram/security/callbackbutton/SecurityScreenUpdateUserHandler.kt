package presentation.telegram.security.callbackbutton

import domain.tinkoff.usecase.FindSecurityUseCase
import domain.user.model.User
import presentation.telegram.core.UiError
import presentation.telegram.core.screen.BotScreen
import presentation.telegram.core.screen.ErrorScreen
import presentation.telegram.security.screen.SecurityScreen
import presentation.telegram.security.search.screen.TickerSearchResultScreen

class SecurityScreenUpdateUserHandler(
    private val findSecurity: FindSecurityUseCase
) {
    suspend fun handle(user: User, messageId: Long?, updateUser: suspend () -> User?): BotScreen {
        val updatedUser = updateUser() ?: return ErrorScreen(user.id, UiError.UnregisteredUserError)

        user.ticker?.let { ticker ->
            return when (val security = findSecurity(ticker)) {
                is FindSecurityUseCase.Result.Success -> SecurityScreen(
                    user = updatedUser,
                    security = security.security,
                    lastPrice = security.price,
                    messageId = messageId
                )

                else -> SecurityScreen(
                    user = updatedUser,
                    security = null,
                    lastPrice = null,
                    messageId = messageId
                )
            }
        }

        return TickerSearchResultScreen(
            userId = updatedUser.id,
            messageId = messageId,
            searchResult = FindSecurityUseCase.Result.NotFound
        )
    }
}