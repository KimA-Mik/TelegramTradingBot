package presentation.agent

import domain.agent.useCase.SetAgentBotInfoUseCase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import ru.mail.im.botapi.fetcher.event.Event

class AgentBotModel(
    private val setAgentBotInfoUseCase: SetAgentBotInfoUseCase,
    private val eventHandler: AgentEventHandler
) {
    private val visitor = AgentVisitor()
    private val botEventFlow = MutableSharedFlow<AgentBotEvent>()
    private val scope = CoroutineScope(Dispatchers.Default)

    private val _outScreens = MutableSharedFlow<AgentScreen>()
    val outScreens = _outScreens.asSharedFlow()

    init {
        scope.launch {
            botEventFlow.collect { event ->
                val resScreen = eventHandler.handleEvent(event)
                resScreen?.let {
                    _outScreens.emit(it)
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