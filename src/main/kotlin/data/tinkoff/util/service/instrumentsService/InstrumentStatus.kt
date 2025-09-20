package data.tinkoff.util.service.instrumentsService

typealias TInstrumentStatus = ru.tinkoff.piapi.contract.v1.InstrumentStatus

/**
 * Тип площадки торговли
 * @see <a href="https://developer.tbank.ru/invest/services/instruments/methods#instrumentstatus">Reference</a>
 */
enum class InstrumentStatus {
    /**
     * Значение не определено
     */
    INSTRUMENT_STATUS_UNSPECIFIED,

    /**
     * По умолчанию — базовый список инструментов, которыми можно торговать через T-Invest API. Сейчас списки доступных бумаг в API и других интерфейсах совпадают — кроме внебиржевых бумаг, но в будущем списки могут различаться.
     */
    INSTRUMENT_STATUS_BASE,

    /**
     * Список всех инструментов
     * */
    INSTRUMENT_STATUS_ALL,
    UNRECOGNIZED;

    fun toTInstrumentStatus(): TInstrumentStatus = when (this) {
        INSTRUMENT_STATUS_UNSPECIFIED -> TInstrumentStatus.INSTRUMENT_STATUS_UNSPECIFIED
        INSTRUMENT_STATUS_BASE -> TInstrumentStatus.INSTRUMENT_STATUS_BASE
        INSTRUMENT_STATUS_ALL -> TInstrumentStatus.INSTRUMENT_STATUS_ALL
        UNRECOGNIZED -> TInstrumentStatus.UNRECOGNIZED
    }
}