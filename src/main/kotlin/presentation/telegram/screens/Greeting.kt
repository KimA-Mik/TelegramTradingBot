package presentation.telegram.screens

class Greeting(id: Long) : BotScreen(id) {
    override val text = "Добро пожаловать в торговый бот."
    override val parseMode = null
    override val replyMarkup = null
}
