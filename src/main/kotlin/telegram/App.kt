package telegram

import com.github.kotlintelegrambot.Bot
import com.github.kotlintelegrambot.bot
import com.github.kotlintelegrambot.dispatch
import com.github.kotlintelegrambot.dispatcher.command
import com.github.kotlintelegrambot.dispatcher.text
import com.github.kotlintelegrambot.entities.ChatId
import kotlinx.coroutines.*

class App(private val botToken: String) {
    private lateinit var botJob: Job
    private lateinit var telegramBot: Bot
    private lateinit var model: BotModel
    private lateinit var mainScope: CoroutineScope

    suspend fun run() = coroutineScope {
        mainScope = this
        model = BotModel()
        initBot(mainScope)
    }

    private suspend fun initBot(scope: CoroutineScope) {
        println("Starting bot")
        telegramBot = bot {
            token = botToken
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
        botJob.cancel()
    }
}