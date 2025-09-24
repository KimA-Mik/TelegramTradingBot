package ru.kima.cacheserver.api.api

import io.ktor.resources.Resource
import ru.kima.cacheserver.api.schema.model.requests.InstrumentsRequest

@Suppress("UNUSED")
object ApiResources {
    @Resource("/$TRADABLE_SHARES")
    class TradableShares(val instrumentsRequest: InstrumentsRequest)

    @Resource("/$TRADABLE_FUTURES")
    class TradableFutures(val instrumentsRequest: InstrumentsRequest)

    @Resource("/$HISTORIC_CANDLES")
    class HistoricCandles()
}
