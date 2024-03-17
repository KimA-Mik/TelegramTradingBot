package domain.user.useCase

import domain.user.model.UserShare
import domain.user.repository.DatabaseRepository

class GetUserShareUseCase(
    private val repository: DatabaseRepository
) {
    suspend operator fun invoke(userId: Long, ticker: String): GetUserShareResult {
        val shares = repository.getUserShares(userId)
        val share = shares.find { it.ticker == ticker }
            ?: return GetUserShareResult.NotFound(ticker)

        return GetUserShareResult.Success(share)
    }

    sealed interface GetUserShareResult {
        data class Success(
            val share: UserShare,
        ) : GetUserShareResult

        data class NotFound(
            val ticker: String
        ) : GetUserShareResult
    }
}