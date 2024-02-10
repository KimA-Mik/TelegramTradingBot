import di.dataModule
import di.domainModule
import kotlinx.coroutines.runBlocking
import org.koin.core.context.startKoin
import presentation.telegram.App
import java.util.*

fun main() {
    val token = System.getenv("TRADE_BOT")
    if (token == null) {
        println("[ERROR]Provide presentation.telegram bot token via 'TRADE_BOT' environment variable")
        return
    }

    Locale.setDefault(Locale("ru", "RU"))

    startKoin {
        modules(
            dataModule(),
            domainModule()
        )
    }
    runBlocking {
        val app = App(token)
        app.run()
    }
}