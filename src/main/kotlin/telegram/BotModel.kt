package telegram

import Resource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import services.RequestService
import java.text.SimpleDateFormat


class BotModel(private val scope: CoroutineScope) : KoinComponent {
    private val service: RequestService by inject()
    private val dateFormatter = SimpleDateFormat("dd.MM, EEE HH:mm")

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
            "[ОШИБКА] ${securityResponse.message}"
        } else {
            val security = securityResponse.data!!
            if (priceResponse is Resource.Success) {
                val price = priceResponse.data!!
                "${security.symbol} - ${security.description}\n${dateFormatter.format(price.date)} - ${price.price}"
            } else {
                "${security.symbol} - ${security.description}\nЦена не найдена"
            }
        }
        val message = Message(id, outText)
        _outMessage.emit(message)
    }
}