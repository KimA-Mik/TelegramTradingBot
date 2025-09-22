package domain.user.useCase.indicators.result

sealed interface ResetIndicatorResult {
    data object NoShares : ResetIndicatorResult
    data class Success(val newValue: Boolean) : ResetIndicatorResult
}