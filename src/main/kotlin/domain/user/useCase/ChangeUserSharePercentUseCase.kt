package domain.user.useCase

import domain.user.model.UserShare
import domain.user.repository.DatabaseRepository

class ChangeUserSharePercentUseCase(
    private val repository: DatabaseRepository,
    private val getUserShare: GetUserShareUseCase
) {
    suspend operator fun invoke(userId: Long, ticker: String, change: Double): ChangeUserSharePercentResult {
        val share = when (val shareResult = getUserShare(userId, ticker)) {
            is GetUserShareUseCase.GetUserShareResult.Success -> shareResult.share
            is GetUserShareUseCase.GetUserShareResult.NotFound -> return ChangeUserSharePercentResult.NotSubscribed(
                shareResult.ticker
            )
        }

        var newPercent = share.percent + change
        if (newPercent <= 0.0) newPercent = 0.1

        val updatedShare = share.copy(
            percent = newPercent
        )

        val success = repository.updateUserSharePercent(
            userId, updatedShare
        )

        return when (success) {
            true -> ChangeUserSharePercentResult.Success(updatedShare)
            false -> ChangeUserSharePercentResult.NotSubscribed(share.ticker)
        }
    }

    sealed interface ChangeUserSharePercentResult {
        data class Success(
            val share: UserShare,
        ) : ChangeUserSharePercentResult

        data class NotSubscribed(
            val ticker: String
        ) : ChangeUserSharePercentResult
    }
}