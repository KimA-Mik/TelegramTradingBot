package data.di

import data.db.DatabaseConnector
import data.db.UserRepositoryImpl
import domain.user.repository.UserRepository
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

fun dataModule() = module {
    singleOf(::DatabaseConnector)
    singleOf(::UserRepositoryImpl) bind UserRepository::class
}