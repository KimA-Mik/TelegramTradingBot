import kotlinx.coroutines.runBlocking
import okhttp3.OkHttpClient
import okhttp3.Protocol
import org.koin.core.context.startKoin
import org.koin.dsl.module
import services.RequestService
import telegram.App
import util.ConsoleLogger
import util.Logger
import java.util.Locale


fun main(): Unit = runBlocking {
    println("Investing api")
    val token = System.getenv("TRADE_BOT")
    if (token == null) {
        println("[ERROR]Provide telegram bot token via 'TRADE_BOT' environment variable")
        return@runBlocking
    }

    Locale.setDefault(Locale("ru", "RU"))

    startKoin {
        modules(
            module {
                single<Logger> { ConsoleLogger() }
                single {
                    OkHttpClient.Builder().protocols(
                        listOf(Protocol.HTTP_1_1)
                    ).build()
                }
                single { RequestService() }
            }
        )
    }

    val app = App(token)
    app.run()
}
