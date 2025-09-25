package domain.tinkoff.useCase

import Resource
import domain.tinkoff.model.TinkoffShare
import domain.tinkoff.repository.TinkoffRepository

class GetTinkoffShareUseCase(
    private val repository: TinkoffRepository
) {
    suspend operator fun invoke(ticker: String): Resource<TinkoffShare> {
        return repository.getSecurity(ticker)
    }
}