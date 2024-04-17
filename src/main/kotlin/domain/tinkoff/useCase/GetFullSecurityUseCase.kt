package domain.tinkoff.useCase

import domain.common.TAX_MULTIPLIER
import domain.common.getFutureSharePrice
import domain.common.percentBetweenDoubles
import domain.tinkoff.model.DisplayFuture
import domain.tinkoff.model.DisplayShare
import domain.tinkoff.model.TinkoffPrice
import domain.tinkoff.repository.TinkoffRepository
import domain.tinkoff.util.TinkoffFutureComparator
import domain.utils.FuturesUtil
import kotlinx.datetime.toKotlinLocalDateTime

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
        val futuresPrices = repository.getFuturesPrices(futures)
            .data?.associateBy { it.uid }
            ?: emptyMap()

        val sharePrice = sharesPrices.data?.find { it.uid == share.uid } ?: TinkoffPrice()
        val displayShare = DisplayShare(
            ticker = share.ticker,
            name = share.name,
            price = sharePrice.price,
            priceDateTime = sharePrice.dateTime.toKotlinLocalDateTime(),
            futures = futures.map {
                val price = futuresPrices[it.uid]
                val singlePrice = getFutureSharePrice(sharePrice.price, price?.price ?: 0.0)
                val percent = percentBetweenDoubles(singlePrice, sharePrice.price)
                val annualPercent = FuturesUtil.getFutureAnnualPercent(percent, it.expirationDate)
                DisplayFuture(
                    ticker = it.ticker,
                    name = it.ticker,
                    price = price?.price ?: 0.0,
                    priceDateTime = price?.dateTime?.toKotlinLocalDateTime(),
                    percent = percent,
                    annualPercent = annualPercent,
                    annualAfterTaxes = annualPercent * TAX_MULTIPLIER
                )
            }
        )

        return GetSecurityResult.Success(displayShare)
    }

    sealed interface GetSecurityResult {
        data class Success(val displayShare: DisplayShare) : GetSecurityResult
        data object SecurityNotFound : GetSecurityResult
    }
}