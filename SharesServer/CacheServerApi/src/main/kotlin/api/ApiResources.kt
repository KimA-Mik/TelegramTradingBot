package ru.kima.cacheserver.api.api

import io.ktor.resources.Resource
import ru.kima.cacheserver.api.schema.model.requests.InstrumentsRequest

object ApiResources {
    @Resource("/$TRADABLE_SHARES")
    class TradableShares(val instrumentsRequest: InstrumentsRequest)

    @Resource("/$TRADABLE_FUTURES")
    class TradableFutures(val instrumentsRequest: InstrumentsRequest)
}
