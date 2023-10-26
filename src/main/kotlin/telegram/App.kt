package telegram

import com.github.kotlintelegrambot.Bot
import com.github.kotlintelegrambot.bot
import com.github.kotlintelegrambot.dispatch
import com.github.kotlintelegrambot.dispatcher.command
import com.github.kotlintelegrambot.dispatcher.text
import com.github.kotlintelegrambot.entities.ChatId
import kotlinx.coroutines.*
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import services.RequestService

class App(private val bot_token: String) : KoinComponent {
    private val service: RequestService by inject()
    private lateinit var botJob: Job
    private lateinit var telegramBot: Bot
    private lateinit var model: BotModel
    private lateinit var mainScope: CoroutineScope

    suspend fun run() = coroutineScope {
        mainScope = this
//        model = BotModel(mainScope)
//        initBot(mainScope)


        //TODO: Remove launch bloch
        launch {
            val t1 = "SBER"
            val t1Job = async(Dispatchers.IO) { service.getLastPrice(t1) }

            val t2 = "USD/RUB"
            val t2Job = async(Dispatchers.IO) { service.getLastPrice(t2) }
            val p1 = t1Job.await()
            val p2 = t2Job.await()
            if (p1 is Resource.Success) {
                println("$t1 price = ${p1.data!!.price}")
            }
            if (p2 is Resource.Success) {
                println("$t2 price = ${p2.data!!.price}")
            }

            val result = service.getInvestingTickerFutures("SBER")
            if (result is Resource.Success) {
                println(result.data)
            }

            val price = result.data?.let { service.getLastPrice(it.symbol) }
            if (price is Resource.Success) {
                println("$t2 price = ${price.data!!.price}")
            } else {
                if (price != null) {
                    println(price.message)
                }
            }
        }
    }

    private suspend fun initBot(scope: CoroutineScope) {
        telegramBot = bot {
            token = bot_token
            dispatch {
                command("start") {
                    model.dispatchStartMessage(message.chat.id)
                    update.consume()
                }

                command("stop") {
                    bot.sendMessage(chatId = ChatId.fromId(message.chat.id), text = "Stopping bot")
                    onStop()
                    update.consume()
                }

                text {
                    model.handleTextInput(message.chat.id, text)
                }
            }
        }
        telegramBot.startPolling()
        botJob = scope.launch {
            model.outMessage.collect { message ->
                telegramBot.sendMessage(chatId = ChatId.fromId(message.id), text = message.text)
            }
        }
    }

    private fun onStop() {
        telegramBot.stopPolling()
        mainScope.cancel()
    }
}