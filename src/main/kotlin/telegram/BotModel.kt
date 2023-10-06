package telegram

import Event
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import services.RequestService

class BotModel(private val scope: CoroutineScope) {
    private val service = RequestService.get()

    private val _outMessage = MutableStateFlow(Event<Message>(null))
    val outMessage = _outMessage.asStateFlow()
    fun dispatchStartMessage(sender: Long) {
        val message = Message(id = sender, text = "Hi there")
        _outMessage.value = Event(message)
    }

    suspend fun handleTextInput(id: Long, text: String) = coroutineScope {
        val securityJob = async(Dispatchers.IO) { service.getInvestingTicker(text) }
        val priceJob = async(Dispatchers.IO) { service.getLastPrice(text) }

        val security = securityJob.await()
        val price = priceJob.await()
        val outText = if (security.id == -1) {
            "Security not found"
        } else {
            "${security.symbol} - ${security.description}\n${price.date} - ${price.price}"
        }
        val message = Message(id, outText)
        _outMessage.value = Event(message)
    }
}