package ru.kima.cacheserver.api.schema.instrumentsService

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
    UNRECOGNIZED
}
