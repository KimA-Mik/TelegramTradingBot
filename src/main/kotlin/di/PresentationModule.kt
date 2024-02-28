package di

import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module
import presentation.telegram.BotModel
import presentation.telegram.textModels.MySecuritiesTextModel
import presentation.telegram.textModels.RootTextModel
import presentation.telegram.textModels.SearchSecuritiesTextModel

val presentationModule = module {
    singleOf(::BotModel)

    singleOf(::RootTextModel)
    singleOf(::MySecuritiesTextModel)
    singleOf(::SearchSecuritiesTextModel)
}