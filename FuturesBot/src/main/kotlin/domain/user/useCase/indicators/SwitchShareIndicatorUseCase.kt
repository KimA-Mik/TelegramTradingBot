package domain.user.useCase.indicators

import domain.common.Indicator
import domain.user.model.UserShare
import domain.user.repository.DatabaseRepository
import domain.user.useCase.GetUserShareUseCase

class SwitchShareIndicatorUseCase(
    private val database: DatabaseRepository,
    private val getUserShare: GetUserShareUseCase
) {
    suspend operator fun invoke(
        userId: Long,
        ticker: String,
        indicator: Indicator,
        newValue: Boolean
    ): Result {
        val userShare = when (val res = getUserShare(userId, ticker)) {
            is GetUserShareUseCase.GetUserShareResult.NotFound -> return Result.NotSubscribed
            is GetUserShareUseCase.GetUserShareResult.Success -> res.share
        }

        val updated = when (indicator) {
            Indicator.RSI -> userShare.copy(
                rsiNotificationsEnabled = newValue,
                rsiNotified = false
            )

            Indicator.BOLLINGER_BANDS -> userShare.copy(
                bbNotificationsEnabled = newValue,
                bollingerBandsNotified = false
            )
        }

        return when (database.updateUserShare(updated)) {
            true -> Result.Success(updated)
            false -> Result.Error
        }
    }

    sealed interface Result {
        data object NotSubscribed : Result
        data object Error : Result
        data class Success(val userShare: UserShare) : Result
    }
}