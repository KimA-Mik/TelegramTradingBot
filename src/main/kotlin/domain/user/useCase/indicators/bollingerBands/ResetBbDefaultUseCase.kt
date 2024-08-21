package domain.user.useCase.indicators.bollingerBands

import domain.user.model.User
import domain.user.useCase.TransformUserSharesUseCase
import domain.user.useCase.indicators.result.ResetIndicatorResult

class ResetBbDefaultUseCase(private val transformUserShares: TransformUserSharesUseCase) {
    suspend operator fun invoke(user: User): ResetIndicatorResult {
        return when (transformUserShares(user) {
            it.copy(bollingerBandsNotified = false, bbNotificationsEnabled = user.defaultBbNotifications)
        }) {
            true -> ResetIndicatorResult.Success(user.defaultBbNotifications)
            false -> ResetIndicatorResult.NoShares
        }
    }
}