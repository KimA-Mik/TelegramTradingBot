package domain.user.useCase.indicators.rsi

import domain.user.model.User
import domain.user.useCase.TransformUserSharesUseCase
import domain.user.useCase.indicators.result.ResetIndicatorResult

class ResetRsiDefaultUseCase(private val transformUserShares: TransformUserSharesUseCase) {
    suspend operator fun invoke(user: User): ResetIndicatorResult {
        return when (transformUserShares(user) {
            it.copy(rsiNotified = false, rsiNotificationsEnabled = user.defaultRsiNotifications)
        }) {
            true -> ResetIndicatorResult.Success(user.defaultRsiNotifications)
            false -> ResetIndicatorResult.NoShares
        }
    }
}