package presentation.telegram.security.search.callbackbutton

import domain.user.model.User
import domain.user.usecase.SubscribeToDefaultSecuritiesUseCase
import kotlinx.coroutines.flow.flow
import presentation.telegram.core.CallbackButtonHandler
import presentation.telegram.core.screen.BotScreen
import presentation.telegram.security.search.screen.AddDefaultSecuritiesScreen

class AcceptDefaultSecuritiesCallbackButtonHandler(
    private val subscribeToDefaultSecurities: SubscribeToDefaultSecuritiesUseCase,
) : CallbackButtonHandler {
    override suspend fun execute(
        user: User,
        messageId: Long,
        messageText: String,
        arguments: List<String>
    ) = flow<BotScreen> {
        val state = when (val res = subscribeToDefaultSecurities(user)) {
            is SubscribeToDefaultSecuritiesUseCase.Res.Success -> AddDefaultSecuritiesScreen.State.Success(res.count)
            SubscribeToDefaultSecuritiesUseCase.Res.CacheServerError -> AddDefaultSecuritiesScreen.State.Failure
            SubscribeToDefaultSecuritiesUseCase.Res.NoDefaultSecurities -> AddDefaultSecuritiesScreen.State.NoDefaultSecurities
            SubscribeToDefaultSecuritiesUseCase.Res.UnknownError -> AddDefaultSecuritiesScreen.State.Failure
            SubscribeToDefaultSecuritiesUseCase.Res.UnregisteredUser -> AddDefaultSecuritiesScreen.State.Failure
        }

        emit(AddDefaultSecuritiesScreen(user.id, state, messageId))
    }
}