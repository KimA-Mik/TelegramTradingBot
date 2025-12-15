package data.di

import data.db.DatabaseConnector
import data.db.UserRepositoryImpl
import domain.user.repository.UserRepository
import io.ktor.client.plugins.logging.*
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module
import ru.kima.cacheserver.api.api.CacheServerApi

fun dataModule(cacheServerApiUrl: String) = module {
    singleOf(::DatabaseConnector)
    singleOf(::UserRepositoryImpl) bind UserRepository::class
    single { CacheServerApi(cacheServerApiUrl, logLevel = LogLevel.NONE) }
}