package presentation.telegram.security.update

import presentation.telegram.core.screen.BotScreen

abstract class SecurityAlertScreen(id: Long, messageId: Long? = null) : BotScreen(id, messageId) {
    protected var hideProblematicUserNote = false
        private set

    fun fixNoteMarkdown() {
        hideProblematicUserNote = true
    }

    override fun shouldFireError(): Boolean {
        return hideProblematicUserNote
    }
}

