package util

interface Logger {
    fun logMessage(message: String)
    fun logError(message: String)
}