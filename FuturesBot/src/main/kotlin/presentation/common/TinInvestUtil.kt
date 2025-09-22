package presentation.common

object TinInvestUtil {
    private const val URL = "https://www.tinkoff.ru/invest/"

    fun shareUrl(ticker: String) = URL + "stocks/$ticker"
    fun futureUrl(ticker: String) = URL + "futures/$ticker"
}