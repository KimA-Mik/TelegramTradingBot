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
import presentation.telegram.security.edit.screen.EditPriceHeader
import presentation.telegram.security.edit.screen.EditPriceResultScreen
import presentation.telegram.security.edit.screen.EditPriceScreen
import presentation.telegram.security.edit.util.getTickerInEditScreen

class EditLowPriceTextModel(
    private val updateExpectedPrice: UpdateExpectedPriceUseCase,
    private val popUser: PopUserUseCase
) : TextModel {
    override val node = NavigationRoot.SecurityList.SecurityDetails.EditLowPrice
    private val rootTextModel: RootTextModel by inject(RootTextModel::class.java)

    override fun executeCommand(
        user: User,
        path: List<String>,
        command: String
    ): Flow<BotScreen> = flow {
        val priceType = UpdateExpectedPriceUseCase.PriceType.LOW
        if (command.isBlank()) {
            emit(EditPriceHeader(user))
            emit(EditPriceScreen(user.id, priceType))
            return@flow
        }

        val res = user.getTickerInEditScreen()?.let {
            updateExpectedPrice(user, it, command, priceType)
        }
        emit(EditPriceResultScreen(user.id, res?.lowTargetPrice, priceType))
        res?.let {
            popUser(user).onSuccess {
                emitAll(rootTextModel.executeCommand(it, it.pathList, ""))
            }
        }
    }
}