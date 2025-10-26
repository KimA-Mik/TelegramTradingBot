package presentation.telegram.security.edit.textmodel

import domain.user.model.User
import domain.user.usecase.FindTickerForUserUseCase
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
import presentation.telegram.security.edit.screen.EditNoteResultScreen
import presentation.telegram.security.edit.screen.EditNoteScreen
import presentation.telegram.security.edit.util.getTickerInEditScreen

class EditNoteTextModel(
    private val popUser: PopUserUseCase,
    private val updateNote: UpdateNoteUseCase,
    private val findTickerForUser: FindTickerForUserUseCase
) : TextModel {
    override val node = NavigationRoot.SecurityList.SecurityDetails.EditNote
    private val rootTextModel: RootTextModel by inject(RootTextModel::class.java)

    override fun executeCommand(
        user: User,
        path: List<String>,
        command: String
    ): Flow<BotScreen> = flow {
        val ticker = user.getTickerInEditScreen()
        if (ticker == null) {
            emit(ErrorScreen(user.id, UiError.UnknownError))
            return@flow
        }

        val security = findTickerForUser(user.id, ticker)
        if (security == null) {
            emit(ErrorScreen(user.id, UiError.UnsubscribedToSecurity(ticker)))
            return@flow
        }

        if (command.isBlank()) {
            emit(EditNoteScreen(user.id, security.note, security.noteUpdatedMs))
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

        val updatedSecurity = updateNote(user, ticker, note)
        if (updatedSecurity == null) {
            emit(ErrorScreen(user.id, UiError.UnregisteredUserError))
            return@flow
        }

        emit(EditNoteResultScreen(user.id, result))
        popUser(user)
            .onSuccess { emitAll(rootTextModel.executeCommand(it, it.pathList, "")) }
            .onFailure { emit(ErrorScreen(user.id, UiError.UnregisteredUserError)) }
    }
}