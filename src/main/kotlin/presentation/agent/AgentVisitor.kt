package presentation.agent

import kotlinx.coroutines.flow.MutableSharedFlow
import ru.mail.im.botapi.fetcher.event.*

class AgentVisitor : EventVisitor<MutableSharedFlow<AgentBotEvent>, Unit> {
    override fun visitUnknown(p0: UnknownEvent?, p1: MutableSharedFlow<AgentBotEvent>?) {
        if (p0 == null) {
            return
        }
    }

    override fun visitNewMessage(p0: NewMessageEvent?, p1: MutableSharedFlow<AgentBotEvent>?) {
        if (p0 == null) {
            return
        }
    }

    override fun visitNewChatMembers(p0: NewChatMembersEvent?, p1: MutableSharedFlow<AgentBotEvent>?) {
        TODO("Not yet implemented")
    }

    override fun visitLeftChatMembers(p0: LeftChatMembersEvent?, p1: MutableSharedFlow<AgentBotEvent>?) {
        TODO("Not yet implemented")
    }

    override fun visitDeletedMessage(p0: DeletedMessageEvent?, p1: MutableSharedFlow<AgentBotEvent>?) {
        TODO("Not yet implemented")
    }

    override fun visitEditedMessage(p0: EditedMessageEvent?, p1: MutableSharedFlow<AgentBotEvent>?) {
        TODO("Not yet implemented")
    }

    override fun visitPinnedMessage(p0: PinnedMessageEvent?, p1: MutableSharedFlow<AgentBotEvent>?) {
        TODO("Not yet implemented")
    }

    override fun visitUnpinnedMessage(p0: UnpinnedMessageEvent?, p1: MutableSharedFlow<AgentBotEvent>?) {
        TODO("Not yet implemented")
    }

    override fun visitCallbackQuery(p0: CallbackQueryEvent?, p1: MutableSharedFlow<AgentBotEvent>?) {
        TODO("Not yet implemented")
    }
}