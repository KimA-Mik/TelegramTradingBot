package presentation.telegram.callbackButtons

import domain.user.useCase.GetUserSharesUseCase
import presentation.telegram.screens.BotScreen
import presentation.telegram.screens.ErrorScreen
import presentation.telegram.screens.MySecuritiesList

class SecuritiesListForwardButtonHandler(
    private val getUserShares: GetUserSharesUseCase
) : CallbackButtonHandler {
    override suspend fun execute(
        userId: Long,
        messageId: Long,
        messageText: String,
        arguments: List<String>
    ): BotScreen {
        val page =
            try {
                arguments.first().toInt() + 1
            } catch (e: Exception) {
                return ErrorScreen(id = userId, UNKNOWN_BUTTON_ERROR)
            }

        return when (val result = getUserShares(userId, page)) {
            GetUserSharesUseCase.GetUserSharesResult.NotFound -> MySecuritiesList(userId, messageId)
            is GetUserSharesUseCase.GetUserSharesResult.Success -> MySecuritiesList(
                id = userId,
                messageId = messageId,
                shares = result.shares,
                page = result.page,
                pageSize = result.pageSize,
                totalPages = result.totalPages
            )
        }
    }
}