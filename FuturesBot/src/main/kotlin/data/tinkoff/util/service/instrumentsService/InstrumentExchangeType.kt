package data.tinkoff.util.service.instrumentsService

typealias TInstrumentExchangeType = ru.tinkoff.piapi.contract.v1.InstrumentExchangeType

/**
 * Тип площадки торговли
 * @see <a href="https://developer.tbank.ru/invest/services/instruments/methods#instrumentexchangetype">Reference</a>
 */
enum class InstrumentExchangeType {
    /**
     * Площадка торговли не определена.
     */
    INSTRUMENT_EXCHANGE_UNSPECIFIED,

    /**
     * Бумага, торгуемая у дилера.
     */
    INSTRUMENT_EXCHANGE_DEALER,
    UNRECOGNIZED;

    fun toTInstrumentExchangeType(): TInstrumentExchangeType =
        when (this) {
            INSTRUMENT_EXCHANGE_UNSPECIFIED -> TInstrumentExchangeType.INSTRUMENT_EXCHANGE_UNSPECIFIED
            INSTRUMENT_EXCHANGE_DEALER -> TInstrumentExchangeType.INSTRUMENT_EXCHANGE_DEALER
            UNRECOGNIZED -> TInstrumentExchangeType.INSTRUMENT_EXCHANGE_UNSPECIFIED
        }
}