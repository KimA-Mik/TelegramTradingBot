package presentation.telegram.textModels

import domain.user.model.User
import presentation.telegram.BotScreen

interface TextModel {
    suspend fun executeCommand(user: User, path: List<String>, command: String): BotScreen
}