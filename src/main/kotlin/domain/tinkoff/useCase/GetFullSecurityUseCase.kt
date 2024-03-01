package domain.tinkoff.useCase

import domain.tinkoff.model.FullTinkoffSecurity
import domain.tinkoff.model.TinkoffFuture
import domain.tinkoff.model.TinkoffPrice
import domain.tinkoff.model.TinkoffSecurity
import domain.tinkoff.repository.TinkoffRepository
import kotlin.math.abs

class GetFullSecurityUseCase(private val repository: TinkoffRepository) {
    suspend operator fun invoke(ticker: String): GetSecurityResult {
        val shareResource = repository.getSecurity(ticker)
        if (shareResource.data == null) {
            return GetSecurityResult.SecurityNotFound
        }

        val share = shareResource.data
        val futuresResource = repository.getSecurityFutures(share)
        var futures = futuresResource.data ?: emptyList<TinkoffFuture>()
        futures = futures.sortedWith { o1, o2 ->
            //TODO: 2029 > 2030 if we simply check the last digit
            val years = try {
                val year1 = o1.ticker.last().code
                val year2 = o2.ticker.last().code
                year1 - year2
            } catch (e: NoSuchElementException) {
                0
            }
            if (abs(years) == 9) return@sortedWith -years
            if (years != 0) return@sortedWith years

            val result = try {
                val month1 = o1.ticker.last { it.isLetter() }.code
                val month2 = o2.ticker.last { it.isLetter() }.code
                month1 - month2
            } catch (e: NoSuchElementException) {
                0
            }
            return@sortedWith result
        }

        println(futures.map { it.ticker })

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
        data class Success(val result: FullTinkoffSecurity) : GetSecurityResult
        data object SecurityNotFound : GetSecurityResult
    }
}