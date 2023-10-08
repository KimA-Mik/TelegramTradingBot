import kotlinx.coroutines.runBlocking
import telegram.App
import java.util.Locale


fun main(): Unit = runBlocking {
    println("Investing api")
    val token = System.getenv("TRADE_BOT")
    if (token == null) {
        println("[ERROR]Provide telegram bot token via 'TRADE_BOT' environment variable")
        return@runBlocking
    }

    Locale.setDefault(Locale("ru", "RU"))
    val app = App(token)
    app.run()
}
