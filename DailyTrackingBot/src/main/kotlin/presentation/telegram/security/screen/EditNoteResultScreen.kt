package presentation.telegram.security.screen

import presentation.telegram.core.screen.BotScreen

class EditNoteResultScreen(
    userId: Long,
    result: Result
) : BotScreen(userId) {
    override val text = when (result) {
        Result.SUCCESS -> "Заметка успешно обновлена"
        Result.DELETED -> "Заметка успешно удалена"
    }

    enum class Result {
        SUCCESS, DELETED
    }
}