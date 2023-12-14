package di

import data.moex.repository.MoexRepositoryImpl
import domain.repository.MoexRepository
import domain.securities.useCase.FindSecurityUseCase
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

fun domainModule() = module {
    single<MoexRepository> { MoexRepositoryImpl(get()) }

    singleOf(::FindSecurityUseCase)
}