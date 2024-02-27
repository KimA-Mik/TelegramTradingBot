package presentation.telegram

sealed class BotScreen(val id: Long) {
    class Greeting(id: Long) : BotScreen(id)
}