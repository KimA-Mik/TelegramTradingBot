package presentation.agent

import domain.agent.useCase.SetAgentBotInfoUseCase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.merge
import kotlinx.coroutines.launch
import ru.mail.im.botapi.fetcher.event.Event

class AgentBotModel(
    private val setAgentBotInfoUseCase: SetAgentBotInfoUseCase,
    private val eventHandler: AgentEventHandler,
    updateHandler: AgentUpdateHandler
) {
    private val visitor = AgentVisitor()
    private val botEventFlow = MutableSharedFlow<AgentBotEvent>()
    private val scope = CoroutineScope(Dispatchers.Default)

    private val outMessages = MutableSharedFlow<AgentScreen>()
    val outScreens = merge(
        outMessages,
        updateHandler.updateScreens
    )

    init {
        scope.launch {
            botEventFlow.collect { event ->
                val resScreen = eventHandler.handleEvent(event)
                resScreen.let {
                    outMessages.emit(it)
                }
            }
        }
    }

    fun initBot(botId: String, botName: String) {
        setAgentBotInfoUseCase(botId, botName)
    }

    fun acceptEvent(event: Event<*>) {
        event.accept(visitor, botEventFlow)
    }

    fun stop() {
        scope.cancel()
    }
}