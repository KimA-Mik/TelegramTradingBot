package presentation.telegram.settings.root.callbackbutton

import com.github.kotlintelegrambot.entities.keyboard.InlineKeyboardButton
import domain.user.usecase.ChangeTimeframesToFireUseCase
import presentation.telegram.core.CallbackButton
import ru.kima.cacheserver.api.util.enumValueOfOrNull

object ChangeTimeframesToFireCallbackButton : CallbackButton("change_timeframes_to_fire") {
    fun getCallbackData(direction: ChangeTimeframesToFireUseCase.Direction) = InlineKeyboardButton.CallbackData(
        text = when (direction) {
            ChangeTimeframesToFireUseCase.Direction.INCREASE -> "Таймфреймы: БОЛЬШЕ"
            ChangeTimeframesToFireUseCase.Direction.DECREASE -> "Таймфреймы: МЕНЬШЕ"
        },
        callbackData = callbackName + QUERY_SEPARATOR + direction
    )

    fun parseCallbackData(arguments: List<String>): CallbackData? {
        val direction = arguments.firstOrNull()?.let {
            enumValueOfOrNull<ChangeTimeframesToFireUseCase.Direction>(it)
        } ?: return null
        return CallbackData(direction)
    }

    data class CallbackData(val direction: ChangeTimeframesToFireUseCase.Direction)
}