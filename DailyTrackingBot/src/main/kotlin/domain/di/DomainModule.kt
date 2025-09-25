package domain.di

import domain.user.usecase.FindUserUseCase
import domain.user.usecase.PopUserUseCase
import domain.user.usecase.RegisterUserUseCase
import domain.user.usecase.UserToRootUseCase
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

fun domainModule() = module {
    singleOf(::FindUserUseCase)
    singleOf(::PopUserUseCase)
    singleOf(::RegisterUserUseCase)
    singleOf(::UserToRootUseCase)
}