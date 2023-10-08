package telegram

import Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.withContext
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import services.RequestService


class BotModel() : KoinComponent {
    private val service: RequestService by inject()

    private val _outMessage = MutableSharedFlow<Message>()
    val outMessage = _outMessage.asSharedFlow()
    suspend fun dispatchStartMessage(sender: Long) {
        val message = Message(id = sender, text = "Hi there")
        _outMessage.emit(message)
    }

    suspend fun handleTextInput(id: Long, text: String) {

        withContext(Dispatchers.IO) {
            val resource = async { service.getMarketData(text) }
            val result = resource.await()

            val outText = if (result is Resource.Success) {
                val securityInfo = result.data!!
                "${securityInfo.security.shortName} (${securityInfo.security.secId}) - ${securityInfo.marketData.last}₽"
            } else {
                result.message!!
            }

            val message = Message(id, outText)
            _outMessage.emit(message)
        }
    }
}