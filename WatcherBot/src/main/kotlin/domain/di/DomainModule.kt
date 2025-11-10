package domain.di

import domain.tinkoff.usecase.GetSecurityUseCase
import domain.updateservice.UpdateService
import domain.user.usecase.*
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

fun domainModule() = module {
    singleOf(::UpdateService)

    //Tinkoff
    singleOf(::GetSecurityUseCase)

    //User
    singleOf(::FindUserUseCase)
    singleOf(::NavigateUserUseCase)
    singleOf(::PopUserUseCase)
    singleOf(::RegisterUserUseCase)
    singleOf(::UserToRootUseCase)
}