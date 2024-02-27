package presentation.telegram

import Resource
import domain.moex.securities.useCase.FindSecurityUseCase
import domain.tinkoff.model.SecurityType
import domain.tinkoff.repository.TinkoffRepository
import domain.useCase.NavigateUserUseCase
import domain.useCase.RegisterUserUseCase
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlin.math.max
import kotlin.math.min


class BotModel(
    private val findSecurity: FindSecurityUseCase,
    private val tinkoffRepository: TinkoffRepository,
    private val registerUser: RegisterUserUseCase,
    private val navigateUser: NavigateUserUseCase
) {

    private val _outMessage = MutableSharedFlow<BotScreen>()
    val outMessage = _outMessage.asSharedFlow()
    suspend fun dispatchStartMessage(sender: Long) {
        val registered = when (val result = registerUser(sender)) {
            is Resource.Success -> BotScreen.Greeting(id = sender)
            is Resource.Error -> BotScreen.Error(id = sender, message = result.message ?: UNKNOWN_ERROR)
        }
        _outMessage.emit(registered)
        _outMessage.emit(BotScreen.Root(id = sender))
    }

    suspend fun handleTextInput(id: Long, text: String) {
        val screen = when (text) {
            BotTextCommands.Root.text -> {
                popBack(id)
            }

            BotTextCommands.MySecurities.text -> {
                navigate(id, BotTextCommands.MySecurities.name)
            }

            BotTextCommands.SearchSecurities.text -> {
                navigate(id, BotTextCommands.SearchSecurities.name)

            }

            BotTextCommands.Pop.text -> {
                popBack(id)
            }

            else -> {
                BotScreen.Error(id, "Я пока не знаю что с этим делать")
            }
        }

        _outMessage.emit(screen)

//        val tickers = text.split(' ')
//        for (ticker in tickers) {
//            val outText = getSecurityDescriptionTinkoff(ticker.trim().uppercase())
//            _outMessage.emit(message)
//        }
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

    private suspend fun navigate(id: Long, direction: String): BotScreen {
        val res = navigateUser(id, direction)
        return dispatchNavResult(id, res)
    }

    private suspend fun popBack(id: Long): BotScreen {
        val res = navigateUser(id = id, pop = true)
        return dispatchNavResult(id, res)
    }

    private fun dispatchNavResult(id: Long, navResult: NavigateUserUseCase.NavResult): BotScreen {
        return when (navResult) {
            is NavigateUserUseCase.NavResult.Success -> pathToScreen(navResult.user.id, navResult.user.path)
            NavigateUserUseCase.NavResult.NoUser -> BotScreen.Error(id, "Пользователь не найден")
            is NavigateUserUseCase.NavResult.Unreachable -> BotScreen.Error(id, "Место недостижимо")
        }
    }

    private fun pathToScreen(id: Long, path: String): BotScreen {
        if (path.isBlank()) return BotScreen.Root(id)
        val actualScreen = path.split('/').last()
        return when (actualScreen) {
            BotTextCommands.MySecurities.name -> BotScreen.MySecurities(id)
            BotTextCommands.SearchSecurities.name -> BotScreen.SearchSecurities(id)
            else -> BotScreen.Error(id, "Где я?")
        }
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

    companion object {
        private const val UNKNOWN_ERROR = "Неизвестная ошибка"
    }
}