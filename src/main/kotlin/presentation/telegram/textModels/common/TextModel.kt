package presentation.telegram.textModels.common

import domain.user.model.User
import presentation.telegram.screens.BotScreen

interface TextModel {
    suspend fun executeCommand(user: User, path: List<String>, command: String): BotScreen
}