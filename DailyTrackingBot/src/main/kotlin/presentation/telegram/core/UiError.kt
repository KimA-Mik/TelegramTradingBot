package presentation.telegram.core

import com.github.kotlintelegrambot.entities.ParseMode
import com.github.kotlintelegrambot.entities.ReplyMarkup

sealed class UiError {
    abstract fun render(): String
    val replyMarkup: ReplyMarkup? = null
    val parseMode: ParseMode? = null

    object UnknownPath : UiError() {
        override fun render() = "Неизвестный путь"
    }

    class UnknownCommand(val command: String) : UiError() {
        override fun render() =
            "Неизвестная команда: $command, eсли потерялись нажмите /$HOME_COMMAND, чтобы вернуться в главное меню"

        companion object {
            const val HOME_COMMAND: String = "Home"
        }
    }

    class TextError(val text: String) : UiError() {
        override fun render() = text
    }

    object UnknownError : UiError() {
        override fun render() = "Неизвестная ошибка"
    }

    object UnregisteredUserError : UiError() {
        override fun render() =
            "Похоже мне стерли память и я вас не помню, напишите команду /start, чтобы я вас записал."
    }

    object BrokenCallbackButton : UiError() {
        override fun render() = "Похоже эта кнопка сломана"
    }

    class UnsubscribedToSecurity(val ticker: String) : UiError() {
        override fun render() = "Вы не подписаны на $ticker"
    }

    object UnableToLoadSecurity : UiError() {
        override fun render() = "Не удалось загрузить информацию о ценной бумаге"
    }
}