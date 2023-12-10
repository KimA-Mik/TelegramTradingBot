package di

import data.moex.repository.RequestServiceImpl
import domain.repository.RequestService
import org.koin.dsl.module

fun domainModule() = module {
    single<RequestService> { RequestServiceImpl(get()) }
}