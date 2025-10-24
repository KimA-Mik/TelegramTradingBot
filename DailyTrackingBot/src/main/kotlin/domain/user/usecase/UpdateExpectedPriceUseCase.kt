package domain.user.usecase

import domain.common.parseToDouble
import domain.user.model.TrackingSecurity
import domain.user.model.User
import domain.user.repository.UserRepository

class UpdateExpectedPriceUseCase(
    private val repository: UserRepository
) {
    suspend operator fun invoke(
        user: User,
        ticker: String,
        inputNumber: String,
        priceType: PriceType
    ): TrackingSecurity? {
        val number = runCatching { inputNumber.parseToDouble() }.getOrElse { return null }
        val fullUser = repository.findFullUserById(user.id) ?: return null
        val security = fullUser.securities.find { it.ticker == ticker } ?: return null

        val copy = when (priceType) {
            PriceType.HIGH -> security.copy(
                targetPrice = number,
                shouldNotify = true,
                shouldNotifyRsi = true,
                shouldNotifyBb = true
            )

            PriceType.LOW -> security.copy(
                lowTargetPrice = number,
                shouldNotify = true,
                shouldNotifyRsi = true,
                shouldNotifyBb = true
            )
        }

        return repository.updateTrackingSecurity(copy).getOrNull()
    }

    enum class PriceType {
        HIGH, LOW
    }
}