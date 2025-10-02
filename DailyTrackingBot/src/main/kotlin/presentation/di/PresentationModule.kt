package presentation.di

import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module
import presentation.telegram.BotModel
import presentation.telegram.CallbackHandler
import presentation.telegram.UpdateHandler
import presentation.telegram.core.RootTextModel
import presentation.telegram.security.callbackbutton.TickerSuggestionCallbackHandler
import presentation.telegram.security.callbackbutton.ToggleShowNoteCallbackHandler
import presentation.telegram.security.textmodel.*

fun presentationModule() = module {
    singleOf(::BotModel)
    singleOf(::CallbackHandler)
    singleOf(::UpdateHandler)
    singleOf(::RootTextModel)

    //Security

    singleOf(::TickerSuggestionCallbackHandler)
    singleOf(::ToggleShowNoteCallbackHandler)

    singleOf(::EditNoteTextModel)
    singleOf(::EditPercentTextModel)
    singleOf(::EditPriceTextModel)
    singleOf(::EditTickerTextModel)
    singleOf(::SecurityTextModel)
}