package domain.tinkoff.useCase

import domain.tinkoff.model.FullTinkoffSecurity
import domain.tinkoff.model.TinkoffPrice
import domain.tinkoff.model.TinkoffSecurity
import domain.tinkoff.repository.TinkoffRepository
import domain.tinkoff.util.TinkoffFutureComparator

class GetFullSecurityUseCase(private val repository: TinkoffRepository) {
    suspend operator fun invoke(ticker: String): GetSecurityResult {
        val shareResource = repository.getSecurity(ticker)
        if (shareResource.data == null) {
            return GetSecurityResult.SecurityNotFound
        }

        val share = shareResource.data
        val futuresResource = repository.getSecurityFutures(share)
        var futures = futuresResource.data ?: emptyList()
        futures = futures.sortedWith(TinkoffFutureComparator)

        val sharesPrices = repository.getSharesPrice(listOf(share))
        val sharePrice = sharesPrices.data?.getOrNull(0) ?: TinkoffPrice()
        val futuresPrices = repository.getFuturesPrices(futures).data ?: emptyList()

        val data = FullTinkoffSecurity(
            security = TinkoffSecurity(
                share = share,
                futures = futures
            ),
            sharePrice = sharePrice,
            futuresPrices = futuresPrices
        )
        return GetSecurityResult.Success(data)
    }

    sealed interface GetSecurityResult {
        data class Success(val fullSecurity: FullTinkoffSecurity) : GetSecurityResult
        data object SecurityNotFound : GetSecurityResult
    }
}