package domain.di

import domain.tinkoff.usecase.FindSecurityUseCase
import domain.tinkoff.usecase.GetSecurityUseCase
import domain.user.usecase.FindUserUseCase
import domain.user.usecase.NavigateUserUseCase
import domain.user.usecase.PopUserUseCase
import domain.user.usecase.RegisterUserUseCase
import domain.user.usecase.StartSecurityConfigureSequenceUseCase
import domain.user.usecase.UpdateExpectedPriceUseCase
import domain.user.usecase.UpdateTickerUseCase
import domain.user.usecase.UserToRootUseCase
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
    singleOf(::UpdateTickerUseCase)
    singleOf(::UserToRootUseCase)
}