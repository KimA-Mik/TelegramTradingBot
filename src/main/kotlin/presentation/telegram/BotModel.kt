package presentation.telegram

import Resource
import domain.securities.useCase.FindSecurityUseCase
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import org.koin.core.component.KoinComponent
import kotlin.math.max
import kotlin.math.min


class BotModel(
    private val findSecurity: FindSecurityUseCase
) : KoinComponent {
//    private val service: MoexRepository by inject()

    private val _outMessage = MutableSharedFlow<Message>()
    val outMessage = _outMessage.asSharedFlow()
    suspend fun dispatchStartMessage(sender: Long) {
        val message = Message(id = sender, text = "Hi there")
        _outMessage.emit(message)
    }

    suspend fun handleTextInput(id: Long, text: String) {
        val tickers = text.split(' ')
        for (ticker in tickers) {
            val outText = getSecurityDescription(ticker.trim().uppercase())
            val message = Message(id, outText)
            _outMessage.emit(message)
        }
    }

    private suspend fun getSecurityDescription(securityId: String): String {
        val result = findSecurity(securityId)
        if (result is Resource.Error) {
            return result.message!!
        }

        val security = result.data!!
        var answer = "[${security.time}]\nАкция (${security.secId}): ${security.shortName} - ${security.price}₽\n"
        if (security.futures.isEmpty()) {
            return answer + "Фьючерсы не найдены"
        }

        for (future in security.futures) {
            val d = future.price / security.price
            val factor = future.lotSize?.toDouble() ?: when (d.toInt()) {
                in 5..15 -> 10.0
                in 50..150 -> 100.0
                in 500..1500 -> 1000.0
                in 5000..15000 -> 10000.0
                else -> 1.0
            }
            val maxValue = max(future.price, security.price * factor)
            val minValue = min(future.price, security.price * factor)
            val diff = (maxValue - minValue) / minValue * 100.0
            answer += "[${future.time}]\nФьючерс (${future.secId}): ${future.shortName} - ${future.price}₽\nРазница: %.2f".format(
                diff
            ) + '%'
            if (diff > 5)
                answer += '❗'
            answer += '\n'
        }
        return answer
    }

//    private suspend fun getFuturesStrFromMetadata(data: SecurityMetadata?): String {
//        if (data == null)
//            return "Не удалост найти метаданные"
//
//        data.description.find { it.value == SecurityMetadataTypes.FUTURES.type }
//            ?: return "Не удалост найти метаданные"
//
//        var result = String()
//        for (board in data.boards) {
//            val marketDataResource = service.getMarketData(board.secId, board.engine, board.market, board.boardId)
//            if (marketDataResource is Resource.Error)
//                continue
//
//            val security = marketDataResource.data?.security!!
//            val marketData = marketDataResource.data.marketData
//            result += "Фьючерс: ${security.shortName} - %.2f₽\n".format(marketData.last)
//        }
//
//        return result.ifEmpty { "Не удалост найти Фьючерс" }
//    }
}