package di

import data.db.repository.DatabaseRepositoryImpl
import data.tinkoff.repository.TinkoffRepositoryImpl
import domain.tinkoff.repository.TinkoffRepository
import domain.tinkoff.useCase.GetFullSecurityUseCase
import domain.tinkoff.useCase.GetTinkoffShareUseCase
import domain.user.navigation.useCase.NavigateUserUseCase
import domain.user.navigation.useCase.PopUserUseCase
import domain.user.navigation.useCase.RegisterUserUseCase
import domain.user.navigation.useCase.UserToRootUseCase
import domain.user.repository.DatabaseRepository
import domain.user.useCase.*
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

val domainModule = module {
    singleOf(::TinkoffRepositoryImpl) bind TinkoffRepository::class
    singleOf(::DatabaseRepositoryImpl) bind DatabaseRepository::class

    singleOf(::GetFullSecurityUseCase)
    singleOf(::GetTinkoffShareUseCase)

    singleOf(::ChangeUserSharePercentUseCase)
    singleOf(::FindUserUseCase)
    singleOf(::GetUserSharesUseCase)
    singleOf(::GetUserShareUseCase)
    singleOf(::IsUserSubscribedUseCase)
    singleOf(::SubscribeUserToShareUseCase)
    singleOf(::UnsubscribeUserFromShareUseCase)

    singleOf(::NavigateUserUseCase)
    singleOf(::PopUserUseCase)
    singleOf(::RegisterUserUseCase)
    singleOf(::UserToRootUseCase)
}