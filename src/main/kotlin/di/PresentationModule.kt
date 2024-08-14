package di

import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module
import presentation.agent.AgentBotModel
import presentation.agent.AgentEventHandler
import presentation.agent.AgentUpdateHandler
import presentation.telegram.BotModel
import presentation.telegram.CallbackHandler
import presentation.telegram.UpdateHandler
import presentation.telegram.callbackButtons.ResetNotificationButtonHandler
import presentation.telegram.callbackButtons.SubscribeButtonHandler
import presentation.telegram.callbackButtons.UnsubscribeButtonHandler
import presentation.telegram.securitiesList.callbackButtonsHandlers.EditShareButtonHandler
import presentation.telegram.securitiesList.callbackButtonsHandlers.SecuritiesListBackButtonHandler
import presentation.telegram.securitiesList.callbackButtonsHandlers.SecuritiesListForwardButtonHandler
import presentation.telegram.securitiesList.callbackButtonsHandlers.SharePercentButtonHandler
import presentation.telegram.securitiesList.textModels.MySecuritiesTextModel
import presentation.telegram.settings.callbackButtonsHandlers.*
import presentation.telegram.settings.callbackButtonsHandlers.indicators.bollingerBands.ResetBbDefaultButtonHandler
import presentation.telegram.settings.callbackButtonsHandlers.indicators.bollingerBands.SwitchBbDefaultButtonHandler
import presentation.telegram.settings.callbackButtonsHandlers.indicators.rsi.ResetRsiDefaultButtonHandler
import presentation.telegram.settings.callbackButtonsHandlers.indicators.rsi.SwitchRsiDefaultButtonHandler
import presentation.telegram.settings.textModels.SettingsTextModel
import presentation.telegram.textModels.RootTextModel
import presentation.telegram.textModels.SearchSecuritiesTextModel

val presentationModule = module {
    singleOf(::BotModel)
    singleOf(::CallbackHandler)
    singleOf(::UpdateHandler)

    singleOf(::AgentBotModel)
    singleOf(::AgentEventHandler)
    singleOf(::AgentUpdateHandler)

    singleOf(::RootTextModel)
    singleOf(::MySecuritiesTextModel)
    singleOf(::SearchSecuritiesTextModel)
    singleOf(::SettingsTextModel)

    singleOf(::EditShareButtonHandler)
    singleOf(::ResetNotificationButtonHandler)
    singleOf(::SecuritiesListBackButtonHandler)
    singleOf(::SecuritiesListForwardButtonHandler)
    singleOf(::SharePercentButtonHandler)
    singleOf(::SubscribeButtonHandler)
    singleOf(::UnsubscribeButtonHandler)
    singleOf(::EditDefaultPercentButtonHandler)

    singleOf(::DisableAgentNotificationsButtonHandler)
    singleOf(::EnableAgentNotificationsButtonHandler)
    singleOf(::LinkAgentAccountButtonHandler)
    singleOf(::UnlinkAgentAccountButtonHandler)

    singleOf(::ResetBbDefaultButtonHandler)
    singleOf(::SwitchBbDefaultButtonHandler)
    singleOf(::ResetRsiDefaultButtonHandler)
    singleOf(::SwitchRsiDefaultButtonHandler)
}