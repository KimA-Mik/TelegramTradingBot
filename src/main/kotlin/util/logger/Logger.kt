package util.logger

interface Logger {
    fun logMessage(message: String)
    fun logError(message: String)
}