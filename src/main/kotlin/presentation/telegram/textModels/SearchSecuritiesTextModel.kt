package presentation.telegram.textModels

import domain.user.model.User
import presentation.telegram.BotScreen

class SearchSecuritiesTextModel : TextModel {
    override suspend fun executeCommand(user: User, path: List<String>, command: String): BotScreen {
        return BotScreen.Error(id = user.id, message = "TODO(SearchSecuritiesTextModel)")
    }
}