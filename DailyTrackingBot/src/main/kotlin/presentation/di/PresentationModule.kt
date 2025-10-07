package presentation.di

import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module
import presentation.telegram.BotModel
import presentation.telegram.CallbackHandler
import presentation.telegram.UpdateHandler
import presentation.telegram.core.RootTextModel
import presentation.telegram.security.callbackbutton.SecurityScreenUpdateUserHandler
import presentation.telegram.security.callbackbutton.ToggleIsActiveCallbackHandler
import presentation.telegram.security.callbackbutton.ToggleRemainActiveCallbackHandler
import presentation.telegram.security.callbackbutton.ToggleShowNoteCallbackHandler
import presentation.telegram.security.search.callbackbutton.SubscribeToSecurityCallbackHandler
import presentation.telegram.security.search.callbackbutton.TickerSuggestionCallbackHandler
import presentation.telegram.security.search.callbackbutton.UnsubscribeFromSecurityCallbackHandler
import presentation.telegram.security.search.textmodel.SearchSecurityTextModel
import presentation.telegram.security.textmodel.EditNoteTextModel
import presentation.telegram.security.textmodel.EditPercentTextModel
import presentation.telegram.security.textmodel.EditPriceTextModel
import presentation.telegram.security.textmodel.SecurityTextModel

fun presentationModule() = module {
    singleOf(::BotModel)
    singleOf(::CallbackHandler)
    singleOf(::UpdateHandler)
    singleOf(::RootTextModel)

    //Security

    //Search
    singleOf(::TickerSuggestionCallbackHandler)
    singleOf(::SubscribeToSecurityCallbackHandler)
    singleOf(::UnsubscribeFromSecurityCallbackHandler)
    singleOf(::SearchSecurityTextModel)

    //SecurityEntry
    singleOf(::SecurityScreenUpdateUserHandler)
    singleOf(::ToggleIsActiveCallbackHandler)
    singleOf(::ToggleRemainActiveCallbackHandler)
    singleOf(::ToggleShowNoteCallbackHandler)

    singleOf(::EditNoteTextModel)
    singleOf(::EditPercentTextModel)
    singleOf(::EditPriceTextModel)
    singleOf(::SecurityTextModel)
}