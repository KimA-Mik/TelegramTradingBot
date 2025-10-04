package presentation.telegram.security.screen

import com.github.kotlintelegrambot.entities.ParseMode
import domain.common.formatToRu
import presentation.telegram.core.screen.BotScreen

class EditPercentResultScreen(
    userId: Long,
    percent: Double?
) : BotScreen(userId) {
    override val text: String =
        if (percent == null) "Не удалось обновить процент" else "Новый процент: *${percent.formatToRu()}%*"
    override val parseMode = ParseMode.MARKDOWN
}