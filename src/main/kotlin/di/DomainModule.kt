package di

import data.db.repository.DatabaseRepositoryImpl
import data.moex.repository.MoexRepositoryImpl
import data.tinkoff.repository.TinkoffRepositoryImpl
import domain.moex.repository.MoexRepository
import domain.moex.securities.useCase.FindSecurityUseCase
import domain.tinkoff.repository.TinkoffRepository
import domain.tinkoff.useCase.GetFullSecurityUseCase
import domain.tinkoff.useCase.GetTinkoffShareUseCase
import domain.user.navigation.useCase.NavigateUserUseCase
import domain.user.navigation.useCase.PopUserUseCase
import domain.user.navigation.useCase.RegisterUserUseCase
import domain.user.navigation.useCase.UserToRootUseCase
import domain.user.repository.DatabaseRepository
import domain.user.useCase.FindUserUseCase
import domain.user.useCase.IsUserSubscribedUseCase
import domain.user.useCase.SubscribeUserToShareUseCase
import domain.user.useCase.UnsubscribeUserFromShareUseCase
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

val domainModule = module {
    singleOf(::MoexRepositoryImpl) bind MoexRepository::class
    singleOf(::TinkoffRepositoryImpl) bind TinkoffRepository::class
    singleOf(::DatabaseRepositoryImpl) bind DatabaseRepository::class

    singleOf(::GetFullSecurityUseCase)
    singleOf(::GetTinkoffShareUseCase)

    singleOf(::FindUserUseCase)
    singleOf(::SubscribeUserToShareUseCase)
    singleOf(::UnsubscribeUserFromShareUseCase)
    singleOf(::IsUserSubscribedUseCase)

    singleOf(::RegisterUserUseCase)
    singleOf(::NavigateUserUseCase)
    singleOf(::UserToRootUseCase)
    singleOf(::PopUserUseCase)

    singleOf(::FindSecurityUseCase)
}