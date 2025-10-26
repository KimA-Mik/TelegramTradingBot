package presentation.telegram.security.edit.screen

import com.github.kotlintelegrambot.entities.InlineKeyboardMarkup
import com.github.kotlintelegrambot.entities.ParseMode
import domain.user.model.PriceProlongation
import domain.user.model.User
import presentation.telegram.core.screen.BotScreen
import presentation.telegram.security.edit.callbackbutton.ChangeDefaultPriceProlongationCallbackButton

class EditPriceHeader(
    user: User,
    messageId: Long? = null,
) : BotScreen(user.id, messageId) {
    override val text = HEADER + when (user.defaultPriceProlongation) {
        PriceProlongation.NONE -> NONE
        PriceProlongation.DAY -> DAY
        PriceProlongation.INFINITE -> INFINITE
    }

    override val replyMarkup = InlineKeyboardMarkup.create(buttons = buildList {
        for (entry in PriceProlongation.entries) {
            if (entry == user.defaultPriceProlongation) continue
            val callbackData = ChangeDefaultPriceProlongationCallbackButton.getCallbackData(entry)
            add(listOf(callbackData))
        }
    })

    override val parseMode = ParseMode.MARKDOWN

    companion object {
        private const val HEADER = "Если бумага не отслеживается, то после обновления цены она "
        private const val NONE = "*отслеживаться не будет.*"
        private const val DAY = "*будет отслеживаться в течение дня.*"
        private const val INFINITE = "*будет отслеживаться постоянно.*"
    }
}