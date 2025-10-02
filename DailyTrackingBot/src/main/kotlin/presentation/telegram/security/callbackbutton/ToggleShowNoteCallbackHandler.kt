package presentation.telegram.security.callbackbutton

import domain.tinkoff.usecase.FindSecurityUseCase
import domain.user.model.User
import domain.user.usecase.UpdateShowNoteUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import presentation.telegram.core.CallbackButtonHandler
import presentation.telegram.core.UiError
import presentation.telegram.core.screen.BotScreen
import presentation.telegram.core.screen.ErrorScreen
import presentation.telegram.security.screen.SecurityScreen
import presentation.telegram.security.screen.TickerSearchResultScreen

class ToggleShowNoteCallbackHandler(
    private val updateShowNote: UpdateShowNoteUseCase,
    private val findSecurity: FindSecurityUseCase
) : CallbackButtonHandler {
    override suspend fun execute(
        user: User,
        messageId: Long,
        messageText: String,
        arguments: List<String>
    ): Flow<BotScreen> = flow {
        val callbackData = ToggleShowNoteCallbackButton.parseCallbackData(arguments)
        if (callbackData == null) {
            emit(ErrorScreen(user.id, UiError.BrokenCallbackButton))
            return@flow
        }

        val updatedUser = updateShowNote(user, callbackData.showNote)
        if (updatedUser == null) {
            emit(ErrorScreen(user.id, UiError.UnregisteredUserError))
            return@flow
        }

        user.ticker?.let { ticker ->
            when (val security = findSecurity(ticker)) {
                is FindSecurityUseCase.Result.Success -> emit(
                    SecurityScreen(
                        user = updatedUser,
                        security = security.security,
                        lastPrice = security.price,
                        messageId = messageId
                    )
                )

                else -> emit(
                    SecurityScreen(
                        user = updatedUser,
                        security = null,
                        lastPrice = null,
                        messageId = messageId
                    )
                )
            }
            return@flow
        }

        emit(
            TickerSearchResultScreen(
                userId = updatedUser.id,
                messageId = messageId,
                searchResult = FindSecurityUseCase.Result.NotFound
            )
        )
    }
}