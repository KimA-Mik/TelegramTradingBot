package presentation.telegram.core.screen

class Greeting(userId: Long) : BotScreen(userId) {
    override val text = "Welcome"
}