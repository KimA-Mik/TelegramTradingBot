import org.koin.core.context.GlobalContext.startKoin
import java.util.Locale
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
suspend fun main(array: Array<String>) {
    var failed = false
    if (array.isEmpty()) {
        println("[ОШИБКА] Для работы программы необходимо предоставить токен для телеграм бота через аргумент")
        failed = true
    }

    if (failed) {
        return
    }

    val ruLocale = Locale.Builder().setLanguage("ru").setRegion("RU").build()
    Locale.setDefault(ruLocale)

//    val dataModule = getDataModule(
//        tinkoffInvestApiToken = tinkoffToken,
//        scope = this
//    )
    startKoin {
//        modules(
//            dataModule,
//            domainModule,
//            presentationModule
//        )
    }
//
//    val telegramBot = TelegramBot(telegramBotApiToken, get(BotModel::class.java))
//    telegramBot.run()
}