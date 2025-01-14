package di

import data.db.DatabaseConnector
import data.tinkoff.service.TinkoffInvestService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.koin.core.module.Module
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module
import ru.tinkoff.piapi.core.InvestApi

fun getDataModule(tinkoffInvestApiToken: String, scope: CoroutineScope): Module {
    val tinkoffInvestService = TinkoffInvestService(InvestApi.create(tinkoffInvestApiToken))
    //TODO: wtf???
    scope.launch { tinkoffInvestService.launchUpdating() }

    return module {
        single { tinkoffInvestService }
        singleOf(::DatabaseConnector)
    }
}

