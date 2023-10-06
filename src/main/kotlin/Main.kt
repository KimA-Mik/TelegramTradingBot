import kotlinx.coroutines.runBlocking
import telegram.App


fun main(): Unit = runBlocking {
    val token = System.getenv("TRADE_BOT")
    if (token == null) {
        println("[ERROR]Provide telegram bot token via 'TRADE_BOT' environment variable")
        return@runBlocking
    }

    val app = App(token)
    app.run()
}
