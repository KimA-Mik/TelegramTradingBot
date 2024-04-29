package presentation.telegram.screens

import presentation.common.formatAndTrim

class SharesResetResult(userId: Long, state: State) : BotScreen(userId) {
    override val text = stateToText(state)
    override val replyMarkup = null
    override val parseMode = null

    private fun stateToText(state: State): String {
        return when (state) {
            State.Empty -> "Ваш список пуст"
            is State.Success -> "Отслеживаемые акции (${state.count}) сброшены до ${state.percent.formatAndTrim(2)}%"
        }
    }

    sealed interface State {
        data class Success(val percent: Double, val count: Int) : State
        data object Empty : State
    }
}