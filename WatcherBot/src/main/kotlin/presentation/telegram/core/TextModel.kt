package presentation.telegram.core

import domain.user.model.User
import kotlinx.coroutines.flow.Flow
import presentation.telegram.core.screen.BotScreen

interface TextModel {
    fun executeCommand(user: User, path: List<String>, command: String): Flow<BotScreen>
    val node: NavigationGraphNode
}