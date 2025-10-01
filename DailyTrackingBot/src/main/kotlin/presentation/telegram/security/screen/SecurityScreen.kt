package presentation.telegram.security.screen

import com.github.kotlintelegrambot.entities.ParseMode
import com.github.kotlintelegrambot.entities.ReplyMarkup
import domain.user.model.User
import presentation.telegram.core.screen.BotScreen
import ru.kima.cacheserver.api.schema.model.Security

class SecurityScreen(
    private val user: User,
    private val security: Security?,
    private val lastPrice: Double?,
    messageId: Long? = null
) : BotScreen(user.id, messageId) {
    override val text = security?.ticker ?: "Нет"
    override val replyMarkup: ReplyMarkup?
        get() = super.replyMarkup
    override val parseMode = ParseMode.MARKDOWN
    override val disableWebPagePreview = true
}