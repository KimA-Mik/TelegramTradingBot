package data.di

import data.config.LocalConfigDataSourceImpl
import data.db.DatabaseConnector
import data.db.UserRepositoryImpl
import domain.config.LocalConfigDataSource
import domain.user.repository.UserRepository
import io.ktor.client.plugins.logging.*
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module
import ru.kima.cacheserver.api.api.CacheServerApi

@OptIn(ExperimentalSerializationApi::class)
fun dataModule(cacheServerApiUrl: String, localConfigFile: String?) = module {
    single {
        Json {
            ignoreUnknownKeys = true
            encodeDefaults = true
            allowTrailingComma = true
            allowComments = true
        }
    }
    singleOf(::DatabaseConnector)
    singleOf(::UserRepositoryImpl) bind UserRepository::class
    single { CacheServerApi(cacheServerApiUrl, logLevel = LogLevel.NONE) }
    single(createdAtStart = true) {
        LocalConfigDataSourceImpl(
            get(),
            localConfigFile
        )
    } bind LocalConfigDataSource::class
}