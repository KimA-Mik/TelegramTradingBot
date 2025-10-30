package domain.user.usecase

import domain.user.model.TrackingSecurity
import domain.user.repository.UserRepository

class FindTickerForUserUseCase(
    private val repository: UserRepository,
) {
    suspend operator fun invoke(userId: Long, ticker: String): TrackingSecurity? {
        val user = repository.findFullUserById(userId) ?: return null
        val normalizedTicker = ticker.trim()
        return user.securities.find { normalizedTicker.equals(it.ticker, ignoreCase = true) }
    }
}