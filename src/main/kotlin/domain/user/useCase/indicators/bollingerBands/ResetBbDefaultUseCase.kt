package domain.user.useCase.indicators.bollingerBands

import domain.user.model.User
import domain.user.repository.DatabaseRepository
import domain.user.useCase.indicators.result.ResetIndicatorResult

class ResetBbDefaultUseCase(private val repository: DatabaseRepository) {
    suspend operator fun invoke(user: User): ResetIndicatorResult {
        val userShares = repository.getUserShares(user.id)
        if (userShares.isEmpty()) return ResetIndicatorResult.NoShares

        val updated = userShares.map { it.copy(bbNotificationsEnabled = user.defaultBbNotifications) }
        repository.updateUserShares(updated)

        return ResetIndicatorResult.Success(user.defaultBbNotifications)
    }
}