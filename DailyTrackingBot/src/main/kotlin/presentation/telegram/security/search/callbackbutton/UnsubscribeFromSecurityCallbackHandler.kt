package presentation.telegram.security.search.callbackbutton

import domain.user.model.User
import domain.user.usecase.PopUserUseCase
import domain.user.usecase.UnsubscribeFromSecurityUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import org.koin.java.KoinJavaComponent.inject
import presentation.telegram.core.CallbackButtonHandler
import presentation.telegram.core.RootTextModel
import presentation.telegram.core.UiError
import presentation.telegram.core.screen.BotScreen
import presentation.telegram.core.screen.ErrorScreen
import presentation.telegram.security.search.screen.TickerSearchResultScreen

class UnsubscribeFromSecurityCallbackHandler(
    private val unsubscribeFromSecurity: UnsubscribeFromSecurityUseCase,
    private val popUser: PopUserUseCase
) : CallbackButtonHandler {
    private val rootTextModel: RootTextModel by inject(RootTextModel::class.java)
    override suspend fun execute(
        user: User,
        messageId: Long,
        messageText: String,
        arguments: List<String>
    ): Flow<BotScreen> = flow {
        val callbackData = UnsubscribeFromSecurityCallbackButton.parseCallbackData(arguments)
        if (callbackData == null) {
            emit(ErrorScreen(user.id, UiError.BrokenCallbackButton))
            return@flow
        }

        val result = unsubscribeFromSecurity(user, callbackData.ticker)
        emit(TickerSearchResultScreen(user.id, messageId, result))

        if (!callbackData.popBack) return@flow
        if (user.pathList.lastOrNull()?.equals(callbackData.ticker, ignoreCase = true) == true) {
            popUser(user).onSuccess {
                emitAll(rootTextModel.executeCommand(it, it.pathList, ""))
            }
        }
    }
}