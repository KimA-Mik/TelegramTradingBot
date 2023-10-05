package telegram

import com.github.kotlintelegrambot.Bot
import com.github.kotlintelegrambot.bot
import com.github.kotlintelegrambot.dispatch
import com.github.kotlintelegrambot.dispatcher.command
import com.github.kotlintelegrambot.dispatcher.text
import com.github.kotlintelegrambot.entities.ChatId
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.runBlocking
import services.RequestService
import java.util.*

class App(private val bot_token: String) {

    fun run() = runBlocking {
        coroutineScope {
            initBot()
        }
        coroutineScope {
            val t1 = "SBER"
            val t1Price = async(Dispatchers.IO) { RequestService.get().getLastPrice(t1) }

            val t2 = "GAZP"
            val t2Price = async(Dispatchers.IO) { RequestService.get().getLastPrice(t2) }
            println("$t1 price = ${t1Price.await()}")
            println("$t2 price = ${t2Price.await()}")
        }
        val date = Date(1696330380000)
        println(date)
    }

    private fun initBot() {
        val telegramBot: Bot = bot {
            token = bot_token
            dispatch {
                command("start") {
                    println("$message\n")
                    val result = bot.sendMessage(chatId = ChatId.fromId(message.chat.id), text = "Hi there!")
                    result.fold({
                        println(it)
                        // do something here with the response
                    }, {
                        // do something with the error
                    })
                }
                //echo
                text {
                    bot.sendMessage(chatId = ChatId.fromId(message.chat.id), text = text)
                }
            }
        }
        telegramBot.startPolling()
    }
}