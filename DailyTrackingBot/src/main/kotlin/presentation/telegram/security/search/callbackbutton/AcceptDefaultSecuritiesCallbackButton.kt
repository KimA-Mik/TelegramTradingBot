package presentation.telegram.security.search.callbackbutton

import com.github.kotlintelegrambot.entities.keyboard.InlineKeyboardButton
import presentation.telegram.core.CallbackButton

object AcceptDefaultSecuritiesCallbackButton : CallbackButton("accept_default_securities") {
    private const val TEXT = "Принять"
    fun getCallbackData() = InlineKeyboardButton.CallbackData(
        text = TEXT,
        callbackName
    )
}