package presentation.telegram.security.search.callbackbutton

import domain.tinkoff.usecase.FindSecurityUseCase
import domain.user.model.User
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import presentation.telegram.core.CallbackButtonHandler
import presentation.telegram.core.UiError
import presentation.telegram.core.screen.BotScreen
import presentation.telegram.core.screen.ErrorScreen
import presentation.telegram.security.search.screen.TickerSearchResultScreen

class TickerSuggestionCallbackHandler(
    private val findSecurity: FindSecurityUseCase,
) : CallbackButtonHandler {
    override suspend fun execute(
        user: User,
        messageId: Long,
        messageText: String,
        arguments: List<String>
    ): Flow<BotScreen> = flow {
        val callbackData = TickerSuggestionCallbackButton.parseCallbackData(arguments)
        if (callbackData == null) {
            emit(ErrorScreen(user.id, UiError.BrokenCallbackButton))
            return@flow
        }

        when (val res = findSecurity(user.id, callbackData.ticker)) {
            is FindSecurityUseCase.Result.Success -> {
                emit(
                    TickerSearchResultScreen(
                        userId = user.id,
                        messageId = messageId,
                        searchResult = res
                    )
                )
            }

            else -> emit(
                TickerSearchResultScreen(
                    userId = user.id,
                    messageId = messageId,
                    searchResult = FindSecurityUseCase.Result.NotFound
                )
            )
        }
    }
}