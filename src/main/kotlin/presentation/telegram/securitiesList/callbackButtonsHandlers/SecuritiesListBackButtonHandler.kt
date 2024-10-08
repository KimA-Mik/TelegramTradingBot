package presentation.telegram.securitiesList.callbackButtonsHandlers

import domain.user.model.User
import domain.user.useCase.GetUserSharesUseCase
import presentation.telegram.callbackButtons.CallbackButtonHandler
import presentation.telegram.callbackButtons.UNKNOWN_BUTTON_ERROR
import presentation.telegram.screens.BotScreen
import presentation.telegram.screens.ErrorScreen
import presentation.telegram.securitiesList.screens.MySecuritiesList

class SecuritiesListBackButtonHandler(
    private val getUserShares: GetUserSharesUseCase
) : CallbackButtonHandler {
    override suspend fun execute(
        user: User,
        messageId: Long,
        messageText: String,
        arguments: List<String>
    ): BotScreen {
        val page =
            try {
                arguments.first().toInt() - 1
            } catch (e: Exception) {
                return ErrorScreen(id = user.id, UNKNOWN_BUTTON_ERROR)
            }

        return when (val result = getUserShares(user.id, page)) {
            GetUserSharesUseCase.GetUserSharesResult.NotFound -> MySecuritiesList(user.id, messageId)
            is GetUserSharesUseCase.GetUserSharesResult.Success -> MySecuritiesList(
                id = user.id,
                messageId = messageId,
                shares = result.shares,
                page = result.page,
                pageSize = result.pageSize,
                totalPages = result.totalPages
            )
        }
    }
}