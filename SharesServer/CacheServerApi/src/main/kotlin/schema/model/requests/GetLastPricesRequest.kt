package ru.kima.cacheserver.api.schema.model.requests

import kotlinx.serialization.Serializable
import ru.kima.cacheserver.api.schema.instrumentsService.InstrumentStatus
import ru.kima.cacheserver.api.schema.marketdataService.LastPriceType

@Serializable
data class GetLastPricesRequest(
    val uids: List<String>,
    val lastPriceType: LastPriceType,
    val instrumentStatus: InstrumentStatus
) {
    companion object {
        fun default(
            uids: List<String>,
            lastPriceType: LastPriceType = LastPriceType.LAST_PRICE_EXCHANGE,
            instrumentStatus: InstrumentStatus = InstrumentStatus.INSTRUMENT_STATUS_BASE
        ): GetLastPricesRequest = GetLastPricesRequest(
            uids = uids,
            lastPriceType = lastPriceType,
            instrumentStatus = instrumentStatus
        )
    }
}
