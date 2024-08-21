import di.domainModule
import di.getDataModule
import di.presentationModule
import kotlinx.coroutines.runBlocking
import org.koin.core.context.startKoin
import org.koin.java.KoinJavaComponent.get
import presentation.telegram.BotModel
import presentation.telegram.TelegramBot
import java.util.*

fun main() = runBlocking {
    var failed = false

    val telegramBotApiToken = System.getenv("TRADE_BOT")
    if (telegramBotApiToken == null) {
        println("[ОШИБКА] Для работы программы необходимо предоставить токен для телеграм бота через переменную среды `TRADE_BOT`")
        failed = true
    }

    val tinkoffToken = System.getenv("TINKOFF_TOKEN")
    if (tinkoffToken == null) {
        println("[ОШИБКА] Для работы программы необходимо предоставить токен для Read-only доступа к Тинькофф-инвестициям через переменную среды `TINKOFF_TOKEN`")
        failed = true
    }

//    val agentBotApiToken = System.getenv("AGENT_BOT_TOKEN")

    if (failed) {
        return@runBlocking
    }

    val ruLocale = Locale.Builder().setLanguage("ru").setRegion("RU").build()
    Locale.setDefault(ruLocale)

    val dataModule = getDataModule(
        tinkoffInvestApiToken = tinkoffToken,
        scope = this
    )
    startKoin {
        modules(
            dataModule,
            domainModule,
            presentationModule
        )
    }

//    agentBotApiToken?.let {
//        val agentBot = AgentBot(
//            token = it,
//            model = get(AgentBotModel::class.java)
//        )
//        agentBot.start()
//    }

    val telegramBot = TelegramBot(telegramBotApiToken, get(BotModel::class.java))
    telegramBot.run()
}