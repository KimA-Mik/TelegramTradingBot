package telegram

import com.github.kotlintelegrambot.Bot
import com.github.kotlintelegrambot.bot
import com.github.kotlintelegrambot.dispatch
import com.github.kotlintelegrambot.dispatcher.command
import com.github.kotlintelegrambot.dispatcher.text
import com.github.kotlintelegrambot.entities.ChatId
import kotlinx.coroutines.*
import services.RequestService

class App(private val bot_token: String) {
    private lateinit var botJob: Job
    private lateinit var telegramBot: Bot
    private lateinit var model: BotModel
    private lateinit var mainScope: CoroutineScope

    suspend fun run() = coroutineScope {
        mainScope = this
        model = BotModel(mainScope)
        initBot(mainScope)

        launch {
            val t1 = "SBER"
            val t1Job = async(Dispatchers.IO) { RequestService.get().getLastPrice(t1) }

            val t2 = "USD/RUB"
            val t2Job = async(Dispatchers.IO) { RequestService.get().getLastPrice(t2) }
            val p1 = t1Job.await()
            val p2 = t2Job.await()
            println("$t1 price = ${p1.price}")
            println("$t2 price = ${p2.price}")
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
            model.outMessage.collect { event ->
                event.getValue()?.let { message ->
                    telegramBot.sendMessage(chatId = ChatId.fromId(message.id), text = message.text)
                }
            }
        }
    }

    private fun onStop() {
        telegramBot.stopPolling()
        mainScope.cancel()
    }
}