package domain.user.useCase

import Resource
import domain.tinkoff.repository.TinkoffRepository
import domain.user.repository.DatabaseRepository

class UnsubscribeUserToShareUseCase(
    private val tinkoff: TinkoffRepository,
    private val database: DatabaseRepository
) {
    suspend operator fun invoke(userId: Long, ticker: String): Resource<Unit> {
        val shareResource = tinkoff.getSecurity(ticker)
        if (shareResource.data == null) return Resource.Error("")
        val share = shareResource.data

    }
}