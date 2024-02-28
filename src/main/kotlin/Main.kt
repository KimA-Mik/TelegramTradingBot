import di.dataModule
import di.domainModule
import di.presentationModule
import org.koin.core.context.startKoin
import org.koin.java.KoinJavaComponent.get
import presentation.telegram.App
import presentation.telegram.BotModel
import java.util.*

suspend fun main() {
    val token = System.getenv("TRADE_BOT")
    if (token == null) {
        println("[ERROR]Provide presentation.telegram bot token via 'TRADE_BOT' environment variable")
        return
    }

    Locale.setDefault(Locale("ru", "RU"))

    startKoin {
        modules(
            presentationModule,
            dataModule,
            domainModule
        )
    }

    val app = App(token, get(BotModel::class.java))
    app.run()
}