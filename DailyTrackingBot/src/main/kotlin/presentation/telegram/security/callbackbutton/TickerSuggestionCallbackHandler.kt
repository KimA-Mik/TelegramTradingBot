package presentation.telegram.security.callbackbutton

import domain.common.PATH_SEPARATOR
import domain.tinkoff.usecase.FindSecurityUseCase
import domain.user.model.User
import domain.user.usecase.UpdateTickerUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import presentation.telegram.core.CallbackButtonHandler
import presentation.telegram.core.RootTextModel
import presentation.telegram.core.UiError
import presentation.telegram.core.screen.BotScreen
import presentation.telegram.core.screen.ErrorScreen
import presentation.telegram.security.screen.TickerSearchResultScreen

class TickerSuggestionCallbackHandler(
    private val findSecurity: FindSecurityUseCase,
    private val updateTicker: UpdateTickerUseCase,
    private val rootTextModel: RootTextModel,
) : CallbackButtonHandler {
    override suspend fun execute(
        user: User,
        messageId: Long,
        messageText: String,
        arguments: List<String>
    ): Flow<BotScreen> = flow {
        val callbackData = TickerSuggestionCallbackButton.parseCallbackQuery(arguments)
        if (callbackData == null) {
            emit(ErrorScreen(user.id, UiError.BrokenCallbackButton))
            return@flow
        }

        when (val res = findSecurity(callbackData.ticker)) {
            is FindSecurityUseCase.Result.Success -> {
                val newUser = updateTicker(user, callbackData.ticker)
                emit(
                    TickerSearchResultScreen(
                        userId = user.id,
                        messageId = messageId,
                        searchResult = res
                    )
                )

                if (newUser.securityConfigureSequence) {
                    emitAll(rootTextModel.executeCommand(newUser, newUser.path.split(PATH_SEPARATOR), ""))
                }
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