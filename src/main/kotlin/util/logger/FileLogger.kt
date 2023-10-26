package util.logger

import java.io.File

class FileLogger(filepath: String):Logger {
    private val out = File(filepath).writer()
    override fun logMessage(message: String) {
        out.write("[Info] $message")
    }

    override fun logError(message: String) {
        out.write("[Error] $message")
    }
}