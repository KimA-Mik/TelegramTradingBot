package presentation.agent

import domain.agent.useCase.SetAgentBotIdUseCase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch
import ru.mail.im.botapi.fetcher.event.Event

class AgentBotModel(
    private val setAgentBotIdUseCase: SetAgentBotIdUseCase
) {
    private val visitor = AgentVisitor()
    private val botEventFlow = MutableSharedFlow<AgentBotEvent>()
    private val scope = CoroutineScope(Dispatchers.Default)

    init {
        scope.launch {
            botEventFlow.collect { event ->
                handleBotEvent(event)
            }
        }
    }

    fun initBotId(botId: String) {
        setAgentBotIdUseCase(botId)
    }

    fun acceptEvent(event: Event<*>) {
        event.accept(visitor, botEventFlow)
    }

    fun stop() {
        scope.cancel()
    }

    private fun handleBotEvent(event: AgentBotEvent) {
        when (event) {
            is AgentBotEvent.NewMessageEvent -> handleNewMessageEvent(event)
        }
    }

    private fun handleNewMessageEvent(event: AgentBotEvent.NewMessageEvent) {
        println(event)
    }
}