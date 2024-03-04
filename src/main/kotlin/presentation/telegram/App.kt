package presentation.telegram

import com.github.kotlintelegrambot.Bot
import com.github.kotlintelegrambot.bot
import com.github.kotlintelegrambot.dispatch
import com.github.kotlintelegrambot.dispatcher.callbackQuery
import com.github.kotlintelegrambot.dispatcher.command
import com.github.kotlintelegrambot.dispatcher.telegramError
import com.github.kotlintelegrambot.dispatcher.text
import com.github.kotlintelegrambot.entities.ChatId
import kotlinx.coroutines.*
import presentation.telegram.callbackButtons.CallbackButton

class App(
    private val botToken: String,
    private val model: BotModel
) {
    private lateinit var botJob: Job
    private lateinit var telegramBot: Bot
    private lateinit var mainScope: CoroutineScope

    suspend fun run() = coroutineScope {
        mainScope = this
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

                callbackQuery(
                    callbackData = CallbackButton.Subscribe.callbackData,
                ) {
                    if (callbackQuery.message == null) return@callbackQuery

                    model.handleCallbackButton(
                        callbackData = CallbackButton.Subscribe.callbackData,
                        userId = callbackQuery.message?.chat?.id ?: 0,
                        messageId = callbackQuery.message?.messageId ?: 0,
                        messageText = callbackQuery.message?.text.orEmpty()
                    )
                }
                callbackQuery(
                    callbackData = CallbackButton.Unsubscribe.callbackData
                ) {
                    if (callbackQuery.message == null) return@callbackQuery

                    model.handleCallbackButton(
                        callbackData = CallbackButton.Unsubscribe.callbackData,
                        userId = callbackQuery.message?.chat?.id ?: 0,
                        messageId = callbackQuery.message?.messageId ?: 0,
                        messageText = callbackQuery.message?.text.orEmpty()
                    )
                }

                telegramError {
                    println(error.getErrorMessage())
                }
            }
        }
        telegramBot.startPolling()
        botJob = scope.launch {
            model.outMessages.collect { screen ->
                screen.messageId?.let {
                    telegramBot.editMessageText(
                        chatId = ChatId.fromId(screen.id),
                        messageId = it,
                        text = screen.text,
                        replyMarkup = screen.replyMarkup,
                        parseMode = screen.parseMode
                    )
                    return@collect
                }

                telegramBot.sendMessage(
                    chatId = ChatId.fromId(screen.id),
                    text = screen.text,
                    replyMarkup = screen.replyMarkup,
                    parseMode = screen.parseMode
                )
            }
        }
    }

    private fun onStop() {
        telegramBot.stopPolling()
        mainScope.cancel()
        botJob.cancel()
    }

    companion object {
        const val ROUBLE = '₽'
    }
}