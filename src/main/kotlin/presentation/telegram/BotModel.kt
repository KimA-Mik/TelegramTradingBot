package presentation.telegram

import Resource
import data.moex.data.emitter.securities.EmitterSecuritiesTypes
import data.moex.data.securityMetadata.SecurityMetadata
import data.moex.data.securityMetadata.SecurityMetadataTypes
import domain.repository.RequestService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.withContext
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import kotlin.math.max
import kotlin.math.min


class BotModel : KoinComponent {
    private val service: RequestService by inject()

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

    private suspend fun getSecurityDescription(securityId: String): String = withContext(Dispatchers.IO) {
        //TODO: Abstract out operations to service
        val metadata = service.getSecurityMetadata(securityId)
        if (metadata is Resource.Error) {
            return@withContext "${metadata.message}"
        }

        val emitterId =
            metadata.data?.description?.find { it.name == SecurityMetadataTypes.EMITTER_ID.type }?.value?.toInt()
                ?: return@withContext getFuturesStrFromMetadata(metadata.data)

        val emitterSecuritiesResource = service.getEmitterSecurities(emitterId)
        if (emitterSecuritiesResource is Resource.Error) {
            return@withContext "${emitterSecuritiesResource.message}"
        }

        val emitterSecurities = emitterSecuritiesResource.data
        val commonShare = emitterSecurities?.find { it.type == EmitterSecuritiesTypes.COMMON_SHARE.type }
            ?: return@withContext "Не удалось найти акцию"

        val futures = emitterSecurities.find { it.type == EmitterSecuritiesTypes.FUTURES.type }
            ?: return@withContext "Не удалось найти фьючерс"

        val commonShareJob = async(Dispatchers.IO) {
            service.getMarketData(
                commonShare.secId,
                commonShare.engine,
                commonShare.market,
                commonShare.primaryBoardId
            )
        }
        val futuresJob = async(Dispatchers.IO) {
            service.getMarketData(
                futures.secId,
                futures.engine,
                futures.market,
                futures.primaryBoardId
            )
        }

        val commonShareResource = commonShareJob.await()
        val futuresResource = futuresJob.await()

        if (commonShareResource is Resource.Error) {
            return@withContext commonShareResource.message!!
        }
        if (futuresResource is Resource.Error) {
            return@withContext futuresResource.message!!
        }

        val commonSecurity = commonShareResource.data!!
        val futuresSecurity = futuresResource.data!!

        val d = futuresSecurity.marketData.last / commonSecurity.marketData.last
        val factor = futuresSecurity.security.lotSize?.toDouble() ?: when (d.toInt()) {
            in 5..15 -> 10.0
            in 50..150 -> 100.0
            else -> 1.0
        }
        val maxValue = max(futuresSecurity.marketData.last, commonSecurity.marketData.last * factor)
        val minValue = min(futuresSecurity.marketData.last, commonSecurity.marketData.last * factor)
        val diff = (maxValue - minValue) / minValue * 100.0

        return@withContext "Акция (${commonSecurity.security.secId}): ${commonSecurity.security.shortName} - ${commonSecurity.marketData.last}₽\n" +
                "Фьючерс (${futuresSecurity.security.secId}): ${futuresSecurity.security.shortName} - ${futuresSecurity.marketData.last}₽\n" +
                "Разница: %.2f".format(diff) + "%"
    }

    private suspend fun getFuturesStrFromMetadata(data: SecurityMetadata?): String {
        if (data == null)
            return "Не удалост найти метаданные"

        data.description.find { it.value == SecurityMetadataTypes.FUTURES.type }
            ?: return "Не удалост найти метаданные"

        var result = String()
        for (board in data.boards) {
            val marketDataResource = service.getMarketData(board.secId, board.engine, board.market, board.boardId)
            if (marketDataResource is Resource.Error)
                continue

            val security = marketDataResource.data?.security!!
            val marketData = marketDataResource.data.marketData
            result += "Фьючерс: ${security.shortName} - %.2f₽\n".format(marketData.last)
        }

        return result.ifEmpty { "Не удалост найти Фьючерс" }
    }
}