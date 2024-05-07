package presentation.agent

import ru.mail.im.botapi.BotApiClient
import ru.mail.im.botapi.BotApiClientController
import ru.mail.im.botapi.fetcher.event.Event

class AgentBot(
    token: String,
    private val model: AgentBotModel
) {
    private val client: BotApiClient = BotApiClient(token)
    private lateinit var controller: BotApiClientController

    fun start() {
        controller = BotApiClientController.startBot(client)

        val selfInfo = controller.selfInfo
        model.initBot(selfInfo.userId, selfInfo.nick)

        client.addOnEventFetchListener { events: List<Event<*>?>? ->
            events?.forEach { event ->
                event?.let {
                    model.acceptEvent(it)
                }
            }
        }
    }

    fun stop() {
        client.stop()
        model.stop()
    }
}