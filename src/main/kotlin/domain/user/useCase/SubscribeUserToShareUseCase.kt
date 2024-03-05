package domain.user.useCase

import Resource
import domain.tinkoff.repository.TinkoffRepository
import domain.user.repository.DatabaseRepository

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

        return when (database.subscribeUserToShare(userId, share)) {
            true -> Resource.Success(Unit)
            false -> Resource.Error("")
        }
    }
}