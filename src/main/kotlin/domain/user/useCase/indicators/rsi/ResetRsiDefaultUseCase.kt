package domain.user.useCase.indicators.rsi

import domain.user.model.User
import domain.user.repository.DatabaseRepository
import domain.user.useCase.indicators.result.ResetIndicatorResult

class ResetRsiDefaultUseCase(private val repository: DatabaseRepository) {
    suspend operator fun invoke(user: User): ResetIndicatorResult {
        val userShares = repository.getUserShares(user.id)
        if (userShares.isEmpty()) return ResetIndicatorResult.NoShares

        val updated = userShares.map {
            it.copy(
                rsiNotified = false,
                rsiNotificationsEnabled = user.defaultRsiNotifications
            )
        }
        repository.updateUserShares(updated)

        return ResetIndicatorResult.Success(user.defaultRsiNotifications)
    }
}