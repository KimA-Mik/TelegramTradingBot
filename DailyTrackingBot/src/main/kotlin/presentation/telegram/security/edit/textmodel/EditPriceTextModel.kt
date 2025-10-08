package presentation.telegram.security.edit.textmodel

import domain.user.model.User
import domain.user.usecase.PopUserUseCase
import domain.user.usecase.UpdateExpectedPriceUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import org.koin.java.KoinJavaComponent.inject
import presentation.telegram.core.NavigationRoot
import presentation.telegram.core.RootTextModel
import presentation.telegram.core.TextModel
import presentation.telegram.core.screen.BotScreen
import presentation.telegram.security.edit.screen.EditPriceResultScreen
import presentation.telegram.security.edit.screen.EditPriceScreen

class EditPriceTextModel(
    private val updateExpectedPrice: UpdateExpectedPriceUseCase,
    private val popUser: PopUserUseCase
) : TextModel {
    override val node = NavigationRoot.SecurityList.SecurityDetails.EditPrice
    private val rootTextModel: RootTextModel by inject(RootTextModel::class.java)

    override fun executeCommand(
        user: User,
        path: List<String>,
        command: String
    ): Flow<BotScreen> = flow {
        if (command.isBlank()) {
            emit(EditPriceScreen(user.id))
            return@flow
        }

        val res = updateExpectedPrice(user, command)
        emit(EditPriceResultScreen(user.id, res.getOrNull()?.targetPrice))
        res.onSuccess { newUser ->
            popUser(newUser).onSuccess {
                emitAll(rootTextModel.executeCommand(it, it.pathList, ""))
            }
        }
    }
}