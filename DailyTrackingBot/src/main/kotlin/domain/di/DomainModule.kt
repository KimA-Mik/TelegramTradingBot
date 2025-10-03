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
    singleOf(::NavigateUserUseCase)
    singleOf(::PopUserUseCase)
    singleOf(::RegisterUserUseCase)
    singleOf(::StartSecurityConfigureSequenceUseCase)
    singleOf(::UpdateExpectedPriceUseCase)
    singleOf(::UpdateIsActiveUseCase)
    singleOf(::UpdateNoteUseCase)
    singleOf(::UpdatePercentUseCase)
    singleOf(::UpdateRemainActiveUseCase)
    singleOf(::UpdateShowNoteUseCase)
    singleOf(::UpdateTickerUseCase)
    singleOf(::UserToRootUseCase)
}