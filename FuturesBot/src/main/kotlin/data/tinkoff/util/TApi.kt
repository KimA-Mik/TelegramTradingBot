package data.tinkoff.util

import data.tinkoff.util.service.MarketDataService
import data.tinkoff.util.service.instrumentsService.InstrumentsService

data class TApi(
    val marketDataService: MarketDataService,
    val instrumentsService: InstrumentsService
)