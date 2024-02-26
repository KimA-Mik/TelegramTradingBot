package presentation.telegram

import com.github.kotlintelegrambot.Bot
import com.github.kotlintelegrambot.bot
import com.github.kotlintelegrambot.dispatch
import com.github.kotlintelegrambot.dispatcher.command
import com.github.kotlintelegrambot.dispatcher.telegramError
import com.github.kotlintelegrambot.dispatcher.text
import com.github.kotlintelegrambot.entities.ChatId
import domain.moex.securities.useCase.FindSecurityUseCase
import domain.tinkoff.repository.TinkoffRepository
import kotlinx.coroutines.*
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import presentation.telegram.mappers.toInlineKeyboardMarkup

//TODO: factor out KoinComponent
class App(private val botToken: String) : KoinComponent {
    private lateinit var botJob: Job
    private lateinit var telegramBot: Bot
    private lateinit var model: BotModel
    private lateinit var mainScope: CoroutineScope

    suspend fun run() = coroutineScope {
        mainScope = this
        val findSecurity: FindSecurityUseCase by inject()
        val tinkoffRepository: TinkoffRepository by inject()
        model = BotModel(findSecurity, tinkoffRepository)
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
//                    update.consume()
                }

                text {
                    model.handleTextInput(message.chat.id, text)
                }

                telegramError {
                    println(error.getErrorMessage())
                }
            }
        }
        telegramBot.startPolling()
        botJob = scope.launch {
            model.outMessage.collect { message ->
                val markup = message.buttonsMarkup?.toInlineKeyboardMarkup()
                telegramBot.sendMessage(chatId = ChatId.fromId(message.id), text = message.text, replyMarkup = markup)
            }
        }
    }

    private fun onStop() {
        telegramBot.stopPolling()
        mainScope.cancel()
        botJob.cancel()
    }
}