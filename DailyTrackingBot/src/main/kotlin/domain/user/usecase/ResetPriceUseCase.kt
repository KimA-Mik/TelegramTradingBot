package domain.user.usecase

import domain.user.model.TrackingSecurity
import domain.user.model.User
import domain.user.repository.UserRepository
import domain.util.MathUtil

class ResetPriceUseCase(
    private val repository: UserRepository
) {
    suspend operator fun invoke(user: User, ticker: String): TrackingSecurity? {
        val fullUser = repository.findFullUserById(user.id) ?: return null
        val security = fullUser.securities.find { it.ticker == ticker } ?: return null

        val updatedSecurity = security.copy(
            targetPrice = MathUtil.PRICE_ZERO,
            lowTargetPrice = MathUtil.PRICE_ZERO,
            isActive = false,
            note = null,
            noteUpdatedMs = null,
            shouldNotify = true
        )
        return repository.updateTrackingSecurity(updatedSecurity).getOrNull()
    }
}