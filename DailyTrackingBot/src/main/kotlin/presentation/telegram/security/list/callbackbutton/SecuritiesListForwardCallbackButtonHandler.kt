package presentation.telegram.security.list.callbackbutton

import domain.user.model.User
import domain.user.usecase.GetUserTrackingSecuritiesUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import presentation.telegram.core.CallbackButtonHandler
import presentation.telegram.core.UiError
import presentation.telegram.core.screen.BotScreen
import presentation.telegram.core.screen.ErrorScreen
import presentation.telegram.security.list.screen.SecurityListScreen

class SecuritiesListForwardCallbackButtonHandler(
    private val getUserTrackingSecurities: GetUserTrackingSecuritiesUseCase
) : CallbackButtonHandler {
    override suspend fun execute(
        user: User,
        messageId: Long,
        messageText: String,
        arguments: List<String>
    ): Flow<BotScreen> = flow {
        val callbackData = SecuritiesListForwardCallbackButton.parseCallbackData(arguments)
        if (callbackData == null) {
            emit(ErrorScreen(userId = user.id, UiError.BrokenCallbackButton))
            return@flow
        }

        val screen = when (val securitiesResult = getUserTrackingSecurities(
            user.id,
            page = callbackData.currentPage + 1
        )) {
            GetUserTrackingSecuritiesUseCase.GetUserSharesResult.NotFound -> SecurityListScreen(
                userId = user.id,
                messageId = messageId,
                securities = emptyList(),
            )

            is GetUserTrackingSecuritiesUseCase.GetUserSharesResult.Success -> SecurityListScreen(
                userId = user.id,
                messageId = messageId,
                securities = securitiesResult.securities,
                page = securitiesResult.page,
                pageSize = securitiesResult.pageSize,
                totalPages = securitiesResult.totalPages
            )
        }
        emit(screen)
    }
}