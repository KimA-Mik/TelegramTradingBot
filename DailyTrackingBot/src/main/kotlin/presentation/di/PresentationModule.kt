package presentation.di

import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module
import presentation.telegram.BotModel
import presentation.telegram.CallbackHandler
import presentation.telegram.MessageErrorHandler
import presentation.telegram.UpdateHandler
import presentation.telegram.core.RootTextModel
import presentation.telegram.security.edit.callbackbutton.*
import presentation.telegram.security.edit.textmodel.*
import presentation.telegram.security.list.callbackbutton.EditSecurityCallbackButtonHandler
import presentation.telegram.security.list.callbackbutton.SecuritiesListBackwardCallbackButtonHandler
import presentation.telegram.security.list.callbackbutton.SecuritiesListForwardCallbackButtonHandler
import presentation.telegram.security.list.textmodel.SecurityListTextModel
import presentation.telegram.security.search.callbackbutton.AcceptDefaultSecuritiesCallbackButtonHandler
import presentation.telegram.security.search.callbackbutton.SubscribeToSecurityCallbackHandler
import presentation.telegram.security.search.callbackbutton.TickerSuggestionCallbackHandler
import presentation.telegram.security.search.callbackbutton.UnsubscribeFromSecurityCallbackHandler
import presentation.telegram.security.search.textmodel.SearchSecurityTextModel
import presentation.telegram.settings.root.callbackbutton.ToggleSrsiAlertCallbackButtonHandler

fun presentationModule() = module {
    singleOf(::BotModel)
    singleOf(::CallbackHandler)
    singleOf(::UpdateHandler)
    singleOf(::RootTextModel)
    singleOf(::MessageErrorHandler)

    /* Security */

    //Search
    singleOf(::TickerSuggestionCallbackHandler)
    singleOf(::AcceptDefaultSecuritiesCallbackButtonHandler)
    singleOf(::SubscribeToSecurityCallbackHandler)
    singleOf(::UnsubscribeFromSecurityCallbackHandler)
    singleOf(::SearchSecurityTextModel)

    //List
    singleOf(::EditSecurityCallbackButtonHandler)
    singleOf(::SecuritiesListForwardCallbackButtonHandler)
    singleOf(::SecuritiesListBackwardCallbackButtonHandler)
    singleOf(::SecurityListTextModel)

    //SecurityEntry
    singleOf(::ChangeDefaultPriceProlongationCallbackHandler)
    singleOf(::ResetPriceCallbackHandler)
    singleOf(::SecurityScreenUpdateUserHandler)
    singleOf(::ToggleIsActiveCallbackHandler)
    singleOf(::ToggleRemainActiveCallbackHandler)
    singleOf(::ToggleShowNoteCallbackHandler)

    singleOf(::EditLowPriceTextModel)
    singleOf(::EditSecurityTextModel)
    singleOf(::EditNoteTextModel)
    singleOf(::EditPercentTextModel)
    singleOf(::EditPriceTextModel)

    //Settings
    singleOf(::ToggleSrsiAlertCallbackButtonHandler)
}