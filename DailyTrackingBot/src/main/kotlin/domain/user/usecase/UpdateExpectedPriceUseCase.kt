package domain.user.usecase

import domain.common.parseToDouble
import domain.user.model.PriceProlongation
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

        var isActive = security.isActive
        var remainActive = security.remainActive

        if (!security.isActive) {
            when (user.defaultPriceProlongation) {
                PriceProlongation.NONE -> {}
                PriceProlongation.DAY -> {
                    isActive = true
                    remainActive = false
                }

                PriceProlongation.INFINITE -> {
                    isActive = true
                    remainActive = true
                }
            }
        }

        val copy = when (priceType) {
            PriceType.HIGH -> security.copy(
                targetPrice = number,
                isActive = isActive,
                remainActive = remainActive,
                shouldNotify = true,
                shouldNotifyRsi = true,
                shouldNotifyBb = true
            )

            PriceType.LOW -> security.copy(
                lowTargetPrice = number,
                isActive = isActive,
                remainActive = remainActive,
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