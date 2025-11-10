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
import org.slf4j.LoggerFactory
import presentation.telegram.core.UiError


class TelegramBot(
    private val botToken: String,
    private val model: BotModel
) {
    private lateinit var botJob: Job
    private lateinit var telegramBot: Bot
    private lateinit var mainScope: CoroutineScope
    private val logger = LoggerFactory.getLogger(this::class.java)

    suspend fun run() = coroutineScope {
        mainScope = this
        initBot(mainScope)
    }

    private fun initBot(scope: CoroutineScope) {
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

                command(UiError.UnknownCommand.HOME_COMMAND) {
                    model.homeCommand(message.chat.id)
                    update.consume()
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
                    ).second?.let { error ->
                        logger.error("Failed to edit message $it for user ${screen.id}: $error")
                    }

                    return@collect
                }

                telegramBot.sendMessage(
                    chatId = ChatId.fromId(screen.id),
                    text = screen.text,
                    parseMode = screen.parseMode,
                    disableWebPagePreview = screen.disableWebPagePreview,
                    replyMarkup = screen.replyMarkup
                ).onError { error ->
                    logger.error("Failed to send message to user ${screen.id}: $error")
                }
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