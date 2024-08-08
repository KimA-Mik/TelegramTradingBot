package presentation.telegram.settings.textModels

import domain.user.model.User
import domain.user.useCase.ResetUserSharesPercentUseCase
import kotlinx.coroutines.flow.flow
import presentation.telegram.callbackButtons.UNABLE_TO_RESET
import presentation.telegram.common.UNKNOWN_COMMAND
import presentation.telegram.screens.BotScreen
import presentation.telegram.screens.ErrorScreen
import presentation.telegram.screens.SharesResetResult
import presentation.telegram.settings.screens.IndicatorsSettings
import presentation.telegram.settings.screens.SettingsAgent
import presentation.telegram.settings.screens.SettingsDefaultPercent
import presentation.telegram.settings.screens.SettingsRoot
import presentation.telegram.textModels.common.TextModel

class SettingsTextModel(
    private val resetUserSharesPercent: ResetUserSharesPercentUseCase
) : TextModel {
    override fun executeCommand(user: User, path: List<String>, command: String) = flow<BotScreen> {
        if (command.isBlank()) {
            emit(SettingsRoot(user))
            return@flow
        }

        val screen = when (command) {
            SettingsTextCommands.DefaultPercent.text -> SettingsDefaultPercent(user.id, user.defaultPercent)
            SettingsTextCommands.ResetPercent.text -> reset(user)
            SettingsTextCommands.AgentSettings.text -> SettingsAgent(user)
            SettingsTextCommands.IndicatorsSettings.text -> IndicatorsSettings(user)
            else -> ErrorScreen(user.id, UNKNOWN_COMMAND)
        }

        emit(screen)
    }

    private suspend fun reset(user: User): BotScreen {
        return when (val res = resetUserSharesPercent(user)) {
            ResetUserSharesPercentUseCase.Result.Error -> ErrorScreen(user.id, UNABLE_TO_RESET)
            ResetUserSharesPercentUseCase.Result.Empty -> SharesResetResult(
                userId = user.id,
                state = SharesResetResult.State.Empty
            )

            is ResetUserSharesPercentUseCase.Result.Success -> SharesResetResult(
                userId = user.id,
                state = SharesResetResult.State.Success(
                    percent = user.defaultPercent,
                    count = res.count
                )
            )
        }
    }


    enum class SettingsTextCommands(val text: String) {
        DefaultPercent("Стандартный процент"),
        ResetPercent("Сбросить процент"),
        AgentSettings("Настройки Agent"),
        IndicatorsSettings("Настройки индикаторов")
    }
}