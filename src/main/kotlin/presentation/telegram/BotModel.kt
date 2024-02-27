package presentation.telegram

import Resource
import domain.moex.securities.useCase.FindSecurityUseCase
import domain.tinkoff.model.SecurityType
import domain.tinkoff.repository.TinkoffRepository
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import org.koin.core.component.KoinComponent
import presentation.telegram.model.Message
import kotlin.math.max
import kotlin.math.min


class BotModel(
    private val findSecurity: FindSecurityUseCase,
    private val tinkoffRepository: TinkoffRepository
) : KoinComponent {

    private val _outMessage = MutableSharedFlow<BotScreen>()
    val outMessage = _outMessage.asSharedFlow()
    suspend fun dispatchStartMessage(sender: Long) {

        val message = BotScreen.Greeting(id = sender)
        _outMessage.emit(message)
    }

    suspend fun handleTextInput(id: Long, text: String) {
        val tickers = text.split(' ')
        for (ticker in tickers) {
//            val outText = getSecurityDescriptionMoex(ticker.trim().uppercase())
            val outText = getSecurityDescriptionTinkoff(ticker.trim().uppercase())
            val message = Message(id, outText)
//            _outMessage.emit(message)
        }
    }

    private fun getSecurityDescriptionTinkoff(ticker: String): String {
        val securityType = tinkoffRepository.findSecurity(ticker)

        return when (securityType) {
            SecurityType.SHARE -> getShareDescription(ticker)
            SecurityType.FUTURE -> getFutureDescription(ticker)
            SecurityType.NONE -> "$ticker не найден"
        }
    }

    private fun getShareDescription(ticker: String): String {
        var result = String()
        val shareResource = tinkoffRepository.getSecurity(ticker)
        if (shareResource is Resource.Error) {
            return "Не получилось найти $ticker из-за ошибки: ${shareResource.message}"
        }
        val share = shareResource.data!!
        result += "$ticker - ${share.name}"
        val futuresRes = tinkoffRepository.getSecurityFutures(share)
        if (futuresRes is Resource.Error) {
            result += "\nФьючерсы не найдены"
            return result
        }

        val futures = futuresRes.data!!
        futures.forEach {
            result += "\n${it.ticker} (${it.lot}) - ${it.name}"
        }
        return result
    }

    private fun getFutureDescription(ticker: String): String {
        return "$ticker это фьючерс"
    }

    private suspend fun getSecurityDescriptionMoex(securityId: String): String {
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
                in 5..50 -> 10.0
                in 50..500 -> 100.0
                in 500..1500 -> 1000.0
                in 1500..5000 -> 2000.0
                in 5000..50000 -> 10000.0
                in 50000..150000 -> 100000.0
                else -> 1.0
            }
            val maxValue = max(future.price, security.price * factor)
            val minValue = min(future.price, security.price * factor)
            val diff = (maxValue - minValue) / minValue * 100.0
            answer += "[${future.time}]\nФьючерс (${future.secId}): ${future.shortName} - ${future.price}₽\n"
            if (future.price != 0.0) {
                answer += "Разница: %.2f".format(diff) + '%'
                if (diff > 5) {
                    answer += '❗'
                }
            }
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