package presentation.telegram

sealed class BotScreen(val id: Long) {
    class Greeting(id: Long) : BotScreen(id)
    class Error(id: Long, val message: String) : BotScreen(id)
    class Root(id: Long) : BotScreen(id)
    class SearchSecurities(id: Long) : BotScreen(id)
    class MySecurities(id: Long) : BotScreen(id)
}