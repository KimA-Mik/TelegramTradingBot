package domain.user.useCase

import Resource
import domain.common.USER_NOT_FOUND_MESSAGE
import domain.tinkoff.repository.TinkoffRepository
import domain.user.repository.DatabaseRepository
import presentation.telegram.callbackButtons.UNABLE_TO_SUBSCRIBE

class SubscribeUserToShareUseCase(
    private val tinkoff: TinkoffRepository,
    private val database: DatabaseRepository
) {
    suspend operator fun invoke(userId: Long, ticker: String): Resource<Unit> {
        val shareRes = tinkoff.getSecurity(ticker)
        if (shareRes.data == null) {
            return Resource.Error(shareRes.message)
        }
        val share = shareRes.data

        val defaultPercent = database.findUser(userId)?.defaultPercent
            ?: return Resource.Error(USER_NOT_FOUND_MESSAGE)

        return when (database.subscribeUserToShare(userId, defaultPercent, share)) {
            true -> Resource.Success(Unit)
            false -> Resource.Error(UNABLE_TO_SUBSCRIBE)
        }
    }
}