package presentation.telegram.settings.screens

import com.github.kotlintelegrambot.entities.ParseMode
import domain.agent.model.AgentBotInfo
import presentation.common.AgentUtil
import presentation.common.TelegramUtil
import presentation.telegram.screens.BotScreen

class AgentLinkRequestScreen(
    userId: Long,
    userCode: String,
    agentBotInfo: AgentBotInfo
) : BotScreen(userId) {
    override val text = markupText(userCode, agentBotInfo)
    override val replyMarkup = null
    override val parseMode = ParseMode.MARKDOWN_V2
    override val disableWebPagePreview = true

    private fun markupText(
        userCode: String,
        agentBotInfo: AgentBotInfo
    ): String {
        val botNick = AgentUtil.nameToAgentNick(agentBotInfo.name)
        val res =
            "Для связи с агентом вам необходимо отправить код ${TelegramUtil.copiableText(userCode)} агент-боту " +
                    TelegramUtil.markdownInlineUrl(botNick, AgentUtil.idToUrl(agentBotInfo.id))

        return res
    }
}