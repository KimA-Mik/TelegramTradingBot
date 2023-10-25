package util

class ConsoleLogger : Logger {
    override fun logMessage(message: String) {
        println("[Info] $message")
    }

    override fun logError(message: String) {
        println("[Error] $message")
    }
}