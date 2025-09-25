package data.remoteservice.mappers

import ru.kima.cacheserver.api.schema.instrumentsService.InstrumentExchangeType

typealias TInstrumentExchangeType = ru.tinkoff.piapi.contract.v1.InstrumentExchangeType

fun InstrumentExchangeType.toTInstrumentExchangeType(): TInstrumentExchangeType =
    when (this) {
        InstrumentExchangeType.INSTRUMENT_EXCHANGE_UNSPECIFIED -> TInstrumentExchangeType.INSTRUMENT_EXCHANGE_UNSPECIFIED
        InstrumentExchangeType.INSTRUMENT_EXCHANGE_DEALER -> TInstrumentExchangeType.INSTRUMENT_EXCHANGE_DEALER
        InstrumentExchangeType.UNRECOGNIZED -> TInstrumentExchangeType.INSTRUMENT_EXCHANGE_UNSPECIFIED
    }
