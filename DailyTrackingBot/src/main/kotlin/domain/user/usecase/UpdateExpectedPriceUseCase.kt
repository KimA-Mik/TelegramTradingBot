package domain.user.usecase

import domain.common.parseToDouble
import domain.user.model.TrackingSecurity
import domain.user.model.User
import domain.user.repository.UserRepository

class UpdateExpectedPriceUseCase(
    private val repository: UserRepository
) {
    suspend operator fun invoke(user: User, ticker: String, inputNumber: String): TrackingSecurity? {
        val number = runCatching { inputNumber.parseToDouble() }.getOrElse { return null }
        val fullUser = repository.findFullUserById(user.id) ?: return null
        val security = fullUser.securities.find { it.ticker == ticker } ?: return null
        return repository.updateTrackingSecurity(
            security.copy(targetPrice = number, shouldNotify = true, shouldNotifyRsi = true)
        ).getOrNull()
    }
}