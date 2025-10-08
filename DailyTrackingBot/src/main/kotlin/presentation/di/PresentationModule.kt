package presentation.di

import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module
import presentation.telegram.BotModel
import presentation.telegram.CallbackHandler
import presentation.telegram.UpdateHandler
import presentation.telegram.core.RootTextModel
import presentation.telegram.security.edit.callbackbutton.SecurityScreenUpdateUserHandler
import presentation.telegram.security.edit.callbackbutton.ToggleIsActiveCallbackHandler
import presentation.telegram.security.edit.callbackbutton.ToggleRemainActiveCallbackHandler
import presentation.telegram.security.edit.callbackbutton.ToggleShowNoteCallbackHandler
import presentation.telegram.security.edit.textmodel.EditNoteTextModel
import presentation.telegram.security.edit.textmodel.EditPercentTextModel
import presentation.telegram.security.edit.textmodel.EditPriceTextModel
import presentation.telegram.security.list.callbackbutton.EditSecurityCallbackButtonHandler
import presentation.telegram.security.list.callbackbutton.SecuritiesListBackwardCallbackButtonHandler
import presentation.telegram.security.list.callbackbutton.SecuritiesListForwardCallbackButtonHandler
import presentation.telegram.security.list.textmodel.SecurityListTextModel
import presentation.telegram.security.search.callbackbutton.SubscribeToSecurityCallbackHandler
import presentation.telegram.security.search.callbackbutton.TickerSuggestionCallbackHandler
import presentation.telegram.security.search.callbackbutton.UnsubscribeFromSecurityCallbackHandler
import presentation.telegram.security.search.textmodel.SearchSecurityTextModel

fun presentationModule() = module {
    singleOf(::BotModel)
    singleOf(::CallbackHandler)
    singleOf(::UpdateHandler)
    singleOf(::RootTextModel)

    /* Security */

    //Search
    singleOf(::TickerSuggestionCallbackHandler)
    singleOf(::SubscribeToSecurityCallbackHandler)
    singleOf(::UnsubscribeFromSecurityCallbackHandler)
    singleOf(::SearchSecurityTextModel)

    //List
    singleOf(::EditSecurityCallbackButtonHandler)
    singleOf(::SecuritiesListForwardCallbackButtonHandler)
    singleOf(::SecuritiesListBackwardCallbackButtonHandler)
    singleOf(::SecurityListTextModel)

    //SecurityEntry
    singleOf(::SecurityScreenUpdateUserHandler)
    singleOf(::ToggleIsActiveCallbackHandler)
    singleOf(::ToggleRemainActiveCallbackHandler)
    singleOf(::ToggleShowNoteCallbackHandler)

    singleOf(::EditNoteTextModel)
    singleOf(::EditPercentTextModel)
    singleOf(::EditPriceTextModel)
}