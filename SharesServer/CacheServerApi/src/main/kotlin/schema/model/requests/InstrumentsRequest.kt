package ru.kima.cacheserver.api.schema.model.requests

import kotlinx.serialization.Serializable
import ru.kima.cacheserver.api.schema.instrumentsService.InstrumentExchangeType
import ru.kima.cacheserver.api.schema.instrumentsService.InstrumentStatus

@Serializable
class InstrumentsRequest(
    val instrumentStatus: InstrumentStatus,
    val instrumentExchangeType: InstrumentExchangeType
) {
    companion object {
        val default = InstrumentsRequest(
            instrumentStatus = InstrumentStatus.INSTRUMENT_STATUS_BASE,
            instrumentExchangeType = InstrumentExchangeType.INSTRUMENT_EXCHANGE_UNSPECIFIED
        )
    }
}