package presentation.telegram

import com.github.kotlintelegrambot.Bot
import com.github.kotlintelegrambot.bot
import com.github.kotlintelegrambot.dispatch
import com.github.kotlintelegrambot.dispatcher.callbackQuery
import com.github.kotlintelegrambot.dispatcher.command
import com.github.kotlintelegrambot.dispatcher.telegramError
import com.github.kotlintelegrambot.dispatcher.text
import com.github.kotlintelegrambot.entities.ChatId
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch


class TelegramBot(
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

                callbackQuery {
                    callbackQuery.message?.let {
                        model.handleCallbackButton(
                            callbackData = callbackQuery.data,
                            userId = it.chat.id,
                            messageId = it.messageId,
                            messageText = it.text.orEmpty()
                        )
                    }
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
                    val chatId = ChatId.fromId(screen.id)
                    telegramBot.editMessageText(
                        chatId = chatId,
                        messageId = it,
                        text = screen.text,
                        parseMode = screen.parseMode,
                        disableWebPagePreview = screen.disableWebPagePreview,
                        replyMarkup = screen.replyMarkup
                    )

                    return@collect
                }

                telegramBot.sendMessage(
                    chatId = ChatId.fromId(screen.id),
                    text = screen.text,
                    parseMode = screen.parseMode,
                    disableWebPagePreview = screen.disableWebPagePreview,
                    replyMarkup = screen.replyMarkup
                )
            }
        }
    }

    //FIXME: эта команда крашит программу и она остается активной но не работающей
    private fun onStop() {
        telegramBot.stopPolling()
        mainScope.cancel()
        botJob.cancel()
    }
}