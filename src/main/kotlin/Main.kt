import di.domainModule
import di.getDataModule
import di.presentationModule
import kotlinx.coroutines.runBlocking
import org.koin.core.context.startKoin
import org.koin.java.KoinJavaComponent.get
import presentation.telegram.App
import presentation.telegram.BotModel
import java.util.*

fun main() = runBlocking {

    val token = System.getenv("TRADE_BOT")
    if (token == null) {
        println("[ERROR]Provide presentation.telegram bot token via 'TRADE_BOT' environment variable")
        return@runBlocking
    }

    Locale.setDefault(Locale("ru", "RU"))

    val dataModule = getDataModule(this)
    startKoin {
        modules(
            dataModule,
            domainModule,
            presentationModule
        )
    }

    val app = App(token, get(BotModel::class.java))
    app.run()
}