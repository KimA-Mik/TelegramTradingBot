package domain.di

import domain.tinkoff.usecase.FindSecurityUseCase
import domain.tinkoff.usecase.GetSecurityUseCase
import domain.user.usecase.*
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

fun domainModule() = module {
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
    singleOf(::UpdateNoteUseCase)
    singleOf(::UpdatePercentUseCase)
    singleOf(::UpdateTickerUseCase)
    singleOf(::UserToRootUseCase)
}