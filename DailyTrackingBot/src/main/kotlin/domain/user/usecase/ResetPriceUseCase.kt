package domain.user.usecase

import domain.user.model.TrackingSecurity
import domain.user.model.User
import domain.user.repository.UserRepository

class ResetPriceUseCase(
    private val repository: UserRepository
) {
    suspend operator fun invoke(user: User, ticker: String): TrackingSecurity? {
        val fullUser = repository.findFullUserById(user.id) ?: return null
        val security = fullUser.securities.find { it.ticker == ticker } ?: return null

        val updatedSecurity = security.copy(
            targetPrice = null,
            lowTargetPrice = null,
            isActive = false,
            note = null,
            noteUpdatedMs = null,
            shouldNotify = true
        )
        return repository.updateTrackingSecurity(updatedSecurity).getOrNull()
    }
}