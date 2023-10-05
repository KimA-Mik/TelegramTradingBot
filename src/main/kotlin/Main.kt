import telegram.App


fun main() {
    val token = System.getenv("TRADE_BOT")
    if (token == null) {
        println("[ERROR]Provide telegram bot token via 'TRADE_BOT' environment variable")
        return
    }

    val app = App(token)
    app.run()
}
