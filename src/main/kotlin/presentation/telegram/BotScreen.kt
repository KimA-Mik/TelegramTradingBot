package presentation.telegram

import presentation.telegram.model.SecuritySearchResultData

sealed class BotScreen(val id: Long) {
    class Greeting(id: Long) : BotScreen(id)
    class Error(id: Long, val message: String) : BotScreen(id)
    class Root(id: Long) : BotScreen(id)
    class SearchSecurities(id: Long) : BotScreen(id)
    class MySecurities(id: Long) : BotScreen(id)
    class SecuritySearchResult(id: Long, val result: SecuritySearchResultData) : BotScreen(id)
    class SecurityNotFound(id: Long, val name: String) : BotScreen(id)
}