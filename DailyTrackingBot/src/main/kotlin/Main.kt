import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.main
import com.github.ajalt.clikt.parameters.options.default
import com.github.ajalt.clikt.parameters.options.help
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.options.required
import data.di.dataModule
import domain.di.domainModule
import kotlinx.coroutines.runBlocking
import org.koin.core.context.GlobalContext.startKoin
import org.koin.java.KoinJavaComponent.get
import presentation.di.presentationModule
import presentation.telegram.BotModel
import presentation.telegram.TelegramBot
import java.util.Locale
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)

class Program : CliktCommand() {
    val telegramToken: String by option("-t", "--token").required().help("Telegram bot token")
    val apiUrl: String by option("-u", "--url").help("Cache server API URL")
        .default("127.0.0.1:6969")
    override fun run() {
        startKoin {
            modules(
                dataModule(apiUrl),
                domainModule(),
                presentationModule()
            )
        }
        runBlocking {
            val telegramBot = TelegramBot(telegramToken, get(BotModel::class.java))
            telegramBot.run()
        }
    }
}

fun main(array: Array<String>) {
    val ruLocale = Locale.Builder().setLanguage("ru").setRegion("RU").build()
    Locale.setDefault(ruLocale)
    Program().main(array)
}