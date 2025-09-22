package presentation.telegram.textModels.common

import domain.user.model.User
import kotlinx.coroutines.flow.Flow
import presentation.telegram.screens.BotScreen

interface TextModel {
    fun executeCommand(user: User, path: List<String>, command: String): Flow<BotScreen>
}