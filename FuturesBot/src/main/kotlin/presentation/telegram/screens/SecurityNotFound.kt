package presentation.telegram.screens

class SecurityNotFound(id: Long, val name: String) : BotScreen(id) {
    override val text = "Акция $name не найдена"
    override val replyMarkup = null
    override val parseMode = null

}
