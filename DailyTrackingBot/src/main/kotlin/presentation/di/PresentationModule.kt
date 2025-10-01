package presentation.di

import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module
import presentation.telegram.BotModel
import presentation.telegram.CallbackHandler
import presentation.telegram.UpdateHandler
import presentation.telegram.core.RootTextModel
import presentation.telegram.security.callbackbutton.TickerSuggestionCallbackHandler
import presentation.telegram.security.textmodel.EditNoteTextModel
import presentation.telegram.security.textmodel.EditPercentTextModel
import presentation.telegram.security.textmodel.EditPriceTextModel
import presentation.telegram.security.textmodel.EditTickerTextModel
import presentation.telegram.security.textmodel.SecurityTextModel

fun presentationModule() = module {
    singleOf(::BotModel)
    singleOf(::CallbackHandler)
    singleOf(::UpdateHandler)
    singleOf(::RootTextModel)

    //Security

    singleOf(::TickerSuggestionCallbackHandler)

    singleOf(::EditNoteTextModel)
    singleOf(::EditPercentTextModel)
    singleOf(::EditPriceTextModel)
    singleOf(::EditTickerTextModel)
    singleOf(::SecurityTextModel)
}