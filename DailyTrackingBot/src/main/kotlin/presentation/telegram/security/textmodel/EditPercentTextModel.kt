package presentation.telegram.security.textmodel

import domain.user.model.User
import domain.user.usecase.PopUserUseCase
import domain.user.usecase.UpdatePercentUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import org.koin.java.KoinJavaComponent.inject
import presentation.telegram.core.NavigationRoot
import presentation.telegram.core.RootTextModel
import presentation.telegram.core.TextModel
import presentation.telegram.core.screen.BotScreen
import presentation.telegram.security.screen.EditPercentResultScreen
import presentation.telegram.security.screen.EditPercentScreen

class EditPercentTextModel(
    private val updatePercent: UpdatePercentUseCase,
    private val popUser: PopUserUseCase,
) : TextModel {
    override val node = NavigationRoot.SecurityList.EditPercentage
    private val rootTextModel: RootTextModel by inject(RootTextModel::class.java)

    override fun executeCommand(
        user: User,
        path: List<String>,
        command: String
    ): Flow<BotScreen> = flow {
        if (command.isBlank()) {
            emit(EditPercentScreen(user.id))
            return@flow
        }

        val res = updatePercent(user, command)
        emit(EditPercentResultScreen(user.id, res.getOrNull()?.targetDeviation))
        res.onSuccess { newUser ->
            popUser(newUser).onSuccess {
                emitAll(rootTextModel.executeCommand(it, it.pathList, ""))
            }
        }
    }
}