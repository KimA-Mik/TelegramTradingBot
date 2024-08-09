package domain.user.useCase.indicators.result

import domain.user.model.User

sealed interface SwitchIndicatorDefaultResult {
    data class Success(val user: User) : SwitchIndicatorDefaultResult
    data object Error : SwitchIndicatorDefaultResult
}