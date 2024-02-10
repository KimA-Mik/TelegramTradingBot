package di

import data.moex.MoexApi
import data.moex.data.emitter.securities.EmitterSecurityResponse
import data.moex.data.history.CharsetInfoHolder
import data.moex.data.history.HistoryHolder
import data.moex.data.history.HistoryResponse
import data.moex.data.security.DataHolder
import data.moex.data.security.SecurityResponse
import data.moex.data.securityMetadata.SecurityMetadataResponse
import io.ktor.client.*
import io.ktor.client.engine.okhttp.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.logging.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic
import kotlinx.serialization.modules.subclass
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module
import ru.tinkoff.piapi.core.InvestApi

fun dataModule() = module {
    single {
        HttpClient(OkHttp) {
            engine {
                config {
                    followRedirects(true)
                    // ...
                }
            }

            install(Logging) {
                level = LogLevel.NONE
            }
            install(ContentNegotiation) {
                json(Json {

                    prettyPrint = true
                    isLenient = true
                    ignoreUnknownKeys = true
                    classDiscriminator = "#class"
                    serializersModule = SerializersModule {
                        polymorphic(HistoryResponse::class) {
                            subclass(HistoryHolder::class)
                            subclass(CharsetInfoHolder::class)
                        }

                        polymorphic(SecurityResponse::class) {
                            subclass(data.moex.data.security.CharsetInfoHolder::class)
                            subclass(DataHolder::class)
                        }

                        polymorphic(SecurityMetadataResponse::class) {
                            subclass(data.moex.data.securityMetadata.CharsetInfoHolder::class)
                            subclass(data.moex.data.securityMetadata.DataHolder::class)
                        }

                        polymorphic(EmitterSecurityResponse::class) {
                            subclass(data.moex.data.emitter.securities.CharsetInfoHolder::class)
                            subclass(data.moex.data.emitter.securities.DataHolder::class)
                        }
                    }
                })
            }
        }
    }
    singleOf(::MoexApi)

    single {
        val tinkoffToken = System.getenv("TINKOFF_TOKEN")
        if (tinkoffToken == null) {
            println("Please provide Tinkoff readonly token via TINKOFF_TOKEN environment variable")
            throw Exception()
        }

        return@single InvestApi.create(tinkoffToken)
    }
}