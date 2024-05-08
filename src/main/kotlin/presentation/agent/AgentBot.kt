package presentation.agent

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import ru.mail.im.botapi.BotApiClient
import ru.mail.im.botapi.BotApiClientController
import ru.mail.im.botapi.api.entity.SendTextRequest
import ru.mail.im.botapi.fetcher.event.Event

class AgentBot(
    token: String,
    private val model: AgentBotModel
) {
    private val client: BotApiClient = BotApiClient(token)
    private lateinit var controller: BotApiClientController
    private val outScope = CoroutineScope(Dispatchers.IO)

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

        outScope.launch {
            model.outScreens.collect { screen ->
                val request = SendTextRequest()
                    .setChatId(screen.chatId)
                    .setText(screen.text)
                controller.sendTextMessage(request)
            }
        }
    }

    fun stop() {
        client.stop()
        model.stop()
    }
}