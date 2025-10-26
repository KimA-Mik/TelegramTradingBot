package presentation.telegram.security.edit.callbackbutton

import com.github.kotlintelegrambot.entities.keyboard.InlineKeyboardButton
import domain.user.model.PriceProlongation
import presentation.telegram.core.CallbackButton
import ru.kima.cacheserver.api.util.enumValueOfOrNull

object ChangeDefaultPriceProlongationCallbackButton : CallbackButton("price_prolongation") {
    fun getCallbackData(newPriceProlongation: PriceProlongation) = InlineKeyboardButton.CallbackData(
        when (newPriceProlongation) {
            PriceProlongation.NONE -> "Не отслеживать"
            PriceProlongation.DAY -> "Отслеживать 1 день"
            PriceProlongation.INFINITE -> "Отслеживать бессрочно"
        },
        callbackName + QUERY_SEPARATOR + newPriceProlongation.name
    )

    fun parseCallbackData(arguments: List<String>): CallbackData? {
        val newPriceProlongationString = arguments.getOrNull(0)
        val newPriceProlongation = enumValueOfOrNull<PriceProlongation>(newPriceProlongationString) ?: return null
        return CallbackData(newPriceProlongation)
    }

    data class CallbackData(val newPriceProlongation: PriceProlongation)
}