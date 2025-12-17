package presentation.telegram.security.search.screen

import com.github.kotlintelegrambot.entities.InlineKeyboardMarkup
import presentation.telegram.core.screen.BotScreen
import presentation.telegram.security.search.callbackbutton.AcceptDefaultSecuritiesCallbackButton

class AddDefaultSecuritiesScreen(
    userId: Long,
    private val state: State,
    messageId: Long? = null
) : BotScreen(userId, messageId) {
    override val text = when (state) {
        is State.AddSecurities -> "Будет добавлено ${state.count} бумаг"
        State.Success -> "Бумаги по умолчанию успешно добавлены в ваш список отслеживания."
        State.Failure -> "Не удалось добавить бумаги по умолчанию. Попробуйте позже."
    }

    override val replyMarkup = when (state) {
        is State.AddSecurities -> _replyMarkup
        else -> null
    }

    companion object {
        private val _replyMarkup = InlineKeyboardMarkup.create(
            listOf(AcceptDefaultSecuritiesCallbackButton.getCallbackData())
        )
    }

    sealed interface State {
        data class AddSecurities(val count: Int) : State
        data object Success : State
        data object Failure : State
    }
}