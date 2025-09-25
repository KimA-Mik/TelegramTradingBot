package di

import data.db.DatabaseConnector
import data.tinkoff.service.TinkoffInvestService
import data.tinkoff.util.TApi
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.koin.core.module.Module
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module
import ru.tinkoff.piapi.contract.v1.InstrumentsServiceGrpc
import ru.tinkoff.piapi.contract.v1.MarketDataServiceGrpc
import ru.ttech.piapi.core.connector.ConnectorConfiguration
import ru.ttech.piapi.core.connector.ServiceStubFactory
import java.util.*

fun getDataModule(tinkoffInvestApiToken: String, scope: CoroutineScope): Module {
    val tinkoffInvestService = TinkoffInvestService(constructTApi(tinkoffInvestApiToken))
    scope.launch { tinkoffInvestService.launchUpdating() }

    return module {
        single { tinkoffInvestService }
        singleOf(::DatabaseConnector)
    }
}

private fun createTProperties(tinkoffInvestApiToken: String): Properties {
    val properties = Properties()
    properties.setProperty("token", tinkoffInvestApiToken)
    return properties
}

private fun constructTApi(tinkoffInvestApiToken: String): TApi {
    val configuration = ConnectorConfiguration
        .loadFromProperties(createTProperties(tinkoffInvestApiToken))
    val unaryServiceFactory = ServiceStubFactory.create(configuration)

    return TApi(
        marketDataService = unaryServiceFactory.newAsyncService(MarketDataServiceGrpc::newStub),
        instrumentsService = unaryServiceFactory.newAsyncService(InstrumentsServiceGrpc::newStub)
    )
}