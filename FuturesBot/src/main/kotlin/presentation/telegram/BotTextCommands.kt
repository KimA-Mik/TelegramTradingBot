package presentation.telegram

enum class BotTextCommands(val text: String) {
    Root("Домой"),
    Pop("Назад"),
    SearchSecurities("Поиск"),
    MySecurities("Мои акции"),
    Settings("Настройки")
}