package presentation.telegram.security.textmodel

import domain.user.model.User
import domain.user.usecase.PopUserUseCase
import domain.user.usecase.UpdateNoteUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import org.koin.java.KoinJavaComponent.inject
import presentation.telegram.core.NavigationRoot
import presentation.telegram.core.RootTextModel
import presentation.telegram.core.TextModel
import presentation.telegram.core.UiError
import presentation.telegram.core.screen.BotScreen
import presentation.telegram.core.screen.ErrorScreen
import presentation.telegram.security.screen.EditNoteResultScreen
import presentation.telegram.security.screen.EditNoteScreen

class EditNoteTextModel(
    private val popUser: PopUserUseCase,
    private val updateNote: UpdateNoteUseCase
) : TextModel {
    override val node = NavigationRoot.SecurityList.EditNote
    private val rootTextModel: RootTextModel by inject(RootTextModel::class.java)

    override fun executeCommand(
        user: User,
        path: List<String>,
        command: String
    ): Flow<BotScreen> = flow {
        if (command.isBlank()) {
            emit(EditNoteScreen(user.id))
            return@flow
        }

        val result: EditNoteResultScreen.Result
        val note = when (command) {
            EditNoteScreen.Commands.Delete.text -> {
                result = EditNoteResultScreen.Result.DELETED
                null
            }

            else -> {
                result = EditNoteResultScreen.Result.SUCCESS
                command
            }
        }

        val updatedUser = updateNote(user, note)
        if (updatedUser == null) {
            emit(ErrorScreen(user.id, UiError.UnregisteredUserError))
            return@flow
        }

        emit(EditNoteResultScreen(user.id, result))
        popUser(updatedUser)
            .onSuccess { emitAll(rootTextModel.executeCommand(it, it.pathList, "")) }
            .onFailure { emit(ErrorScreen(user.id, UiError.UnregisteredUserError)) }
    }
}