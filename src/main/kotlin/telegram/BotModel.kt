package telegram

import Resource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import services.RequestService


class BotModel(private val scope: CoroutineScope) {
    private val service = RequestService.get()

    private val _outMessage = MutableSharedFlow<Message>()
    val outMessage = _outMessage.asSharedFlow()
    fun dispatchStartMessage(sender: Long) = scope.launch {
        val message = Message(id = sender, text = "Hi there")
        _outMessage.emit(message)
    }

    suspend fun handleTextInput(id: Long, text: String) = scope.launch {
        val securityJob = async(Dispatchers.IO) { service.getInvestingTicker(text) }
        val priceJob = async(Dispatchers.IO) { service.getLastPrice(text) }

        val securityResponse = securityJob.await()
        val priceResponse = priceJob.await()
        val outText = if (securityResponse is Resource.Error) {
            "[ERROR] ${securityResponse.message}"
        } else {
            val security = securityResponse.data!!
            if (priceResponse is Resource.Success) {
                val price = priceResponse.data!!
                "${security.symbol} - ${security.description}\n${price.date} - ${price.price}"
            } else {
                "${security.symbol} - ${security.description}\nPrice not found"
            }
        }
        val message = Message(id, outText)
        _outMessage.emit(message)
    }
}