package presentation.telegram.security.screen

import com.github.kotlintelegrambot.entities.ParseMode
import domain.common.formatAndTrim
import presentation.telegram.core.screen.BotScreen

class EditPercentResultScreen(
    userId: Long,
    percent: Double?
) : BotScreen(userId) {
    override val text: String =
        if (percent == null) "Не удалось обновить процент" else "Новый процент: *${percent.formatAndTrim(2)}%*"
    override val parseMode = ParseMode.MARKDOWN
}