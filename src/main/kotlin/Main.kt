import api.moex.data.history.HistoryHolder
import api.moex.data.history.HistoryResponse
import api.moex.data.security.SecurityResponse
import api.moex.data.securityMetadata.SecurityMetadataResponse
import io.ktor.client.*
import io.ktor.client.engine.okhttp.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.logging.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic
import kotlinx.serialization.modules.subclass
import org.koin.core.context.startKoin
import org.koin.dsl.module
import services.RequestService
import services.RequestServiceImpl
import telegram.App
import java.util.*

fun main(): Unit = runBlocking {
    val token = System.getenv("TRADE_BOT")
    if (token == null) {
        println("[ERROR]Provide telegram bot token via 'TRADE_BOT' environment variable")
        return@runBlocking
    }

    Locale.setDefault(Locale("ru", "RU"))

    val httpClient = HttpClient(OkHttp) {
        engine {
            config {
                followRedirects(true)
                // ...
            }
        }

        install(Logging) {
            level = LogLevel.ALL
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
                        subclass(api.moex.data.history.CharsetInfoHolder::class)
                    }

                    polymorphic(SecurityResponse::class) {
                        subclass(api.moex.data.security.CharsetInfoHolder::class)
                        subclass(api.moex.data.security.DataHolder::class)
                    }

                    polymorphic(SecurityMetadataResponse::class) {
                        subclass(api.moex.data.securityMetadata.CharsetInfoHolder::class)
                        subclass(api.moex.data.securityMetadata.DataHolder::class)
                    }
                }
            })
        }
    }

    startKoin {
        modules(
            module {
                single { httpClient }
                single<RequestService> { RequestServiceImpl() }
            }
        )
    }
    val app = App(token)
    app.run()
}
