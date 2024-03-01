package presentation.telegram

import domain.tinkoff.useCase.GetFullSecurityUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import presentation.telegram.screens.BotScreen
import presentation.telegram.screens.Root

class CallbackModel(
    private val getFullSecurityUseCase: GetFullSecurityUseCase
) {
    private val _outFlow = MutableSharedFlow<BotScreen>()
    val outFlow = _outFlow.asSharedFlow()

    suspend fun subscribeToSecurity(userId: Long, ticker: String, messageId: Long): BotScreen = coroutineScope {
        val securityJob = async(Dispatchers.IO) { getFullSecurityUseCase(ticker) }


        return@coroutineScope Root(userId)
    }
}