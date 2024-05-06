package di

import data.agent.AgentService
import data.db.DatabaseConnector
import data.tinkoff.service.TinkoffInvestService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.koin.core.module.Module
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module
import ru.tinkoff.piapi.core.InvestApi

suspend fun getDataModule(tinkoffInvestApiToken: String, scope: CoroutineScope): Module {
    val tinkoffInvestService = TinkoffInvestService(InvestApi.create(tinkoffInvestApiToken))
    scope.launch { tinkoffInvestService.launchUpdating() }

    return module {
        singleOf(::AgentService)
        single { tinkoffInvestService }
        singleOf(::DatabaseConnector)
    }
}

