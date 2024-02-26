package presentation.telegram.mappers

import com.github.kotlintelegrambot.entities.InlineKeyboardMarkup
import com.github.kotlintelegrambot.entities.keyboard.InlineKeyboardButton
import presentation.telegram.model.ButtonRow
import presentation.telegram.model.ButtonsMarkup

fun ButtonsMarkup.toInlineKeyboardMarkup(): InlineKeyboardMarkup? {
    if (rows.isEmpty()) {
        return null
    }

    val converted = rows.map { row ->
        row.toCallbackData()
    }

    return InlineKeyboardMarkup.create(converted)
}

fun ButtonRow.toCallbackData(): List<InlineKeyboardButton.CallbackData> {
    return buttons.map { button ->
        InlineKeyboardButton.CallbackData(text = button.text, callbackData = button.callbackData)
    }
}