package di

import data.db.repository.DatabaseRepositoryImpl
import data.moex.repository.MoexRepositoryImpl
import data.tinkoff.repository.TinkoffRepositoryImpl
import domain.local.repository.DatabaseRepository
import domain.moex.repository.MoexRepository
import domain.moex.securities.useCase.FindSecurityUseCase
import domain.tinkoff.repository.TinkoffRepository
import domain.useCase.NavigateUserUseCase
import domain.useCase.RegisterUserUseCase
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

fun domainModule() = module {
    singleOf(::MoexRepositoryImpl) bind MoexRepository::class
    singleOf(::TinkoffRepositoryImpl) bind TinkoffRepository::class
    singleOf(::DatabaseRepositoryImpl) bind DatabaseRepository::class

    singleOf(::RegisterUserUseCase)
    singleOf(::NavigateUserUseCase)

    singleOf(::FindSecurityUseCase)
}