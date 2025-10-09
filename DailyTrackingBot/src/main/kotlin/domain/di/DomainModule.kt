package domain.di

import domain.tinkoff.usecase.FindSecurityUseCase
import domain.tinkoff.usecase.GetSecurityUseCase
import domain.updateservice.UpdateService
import domain.user.usecase.*
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

fun domainModule() = module {
    singleOf(::UpdateService)

    //Tinkoff
    singleOf(::FindSecurityUseCase)
    singleOf(::GetSecurityUseCase)

    //User
    singleOf(::FindUserUseCase)
    singleOf(::GetUserTrackingSecuritiesUseCase)
    singleOf(::NavigateUserUseCase)
    singleOf(::PopUserUseCase)
    singleOf(::RegisterUserUseCase)
    singleOf(::SubscribeToSecurityUseCase)
    singleOf(::UnsubscribeFromSecurityUseCase)
    singleOf(::UpdateExpectedPriceUseCase)
    singleOf(::UpdateIsActiveUseCase)
    singleOf(::UpdateNoteUseCase)
    singleOf(::UpdatePercentUseCase)
    singleOf(::UpdateRemainActiveUseCase)
    singleOf(::UpdateShowNoteUseCase)
    singleOf(::UserToRootUseCase)
}