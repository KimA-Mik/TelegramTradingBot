package presentation.telegram

import com.github.kotlintelegrambot.Bot
import com.github.kotlintelegrambot.bot
import com.github.kotlintelegrambot.dispatch
import com.github.kotlintelegrambot.dispatcher.command
import com.github.kotlintelegrambot.dispatcher.telegramError
import com.github.kotlintelegrambot.dispatcher.text
import com.github.kotlintelegrambot.entities.ChatId
import com.github.kotlintelegrambot.entities.KeyboardReplyMarkup
import com.github.kotlintelegrambot.entities.ParseMode
import com.github.kotlintelegrambot.entities.ReplyMarkup
import com.github.kotlintelegrambot.entities.keyboard.KeyboardButton
import kotlinx.coroutines.*

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

                telegramError {
                    println(error.getErrorMessage())
                }
            }
        }
        telegramBot.startPolling()
        botJob = scope.launch {
            model.outMessage.collect { screen ->
                telegramBot.sendMessage(
                    chatId = ChatId.fromId(screen.id),
                    text = screenText(screen),
                    replyMarkup = screenReplayMarkup(screen),
                    parseMode = screenParseMode(screen)
                )
            }
        }
    }

    private fun screenText(screen: BotScreen): String = when (screen) {
        is BotScreen.Greeting -> "Добро пожаловать в торговый бот."
        is BotScreen.Error -> "Произошла ошибка: ${screen.message}"
        is BotScreen.Root -> "*Супер бот:*\n• ${BotTextCommands.MySecurities.text}\n• ${BotTextCommands.SearchSecurities.text}"
        is BotScreen.MySecurities -> "TODO(MySecurities)"
        is BotScreen.SearchSecurities -> "TODO(SearchSecurities)"
    }


    private fun screenReplayMarkup(screen: BotScreen): ReplyMarkup? = when (screen) {
        is BotScreen.Root -> KeyboardReplyMarkup(
            listOf(
                listOf(
                    KeyboardButton(BotTextCommands.SearchSecurities.text),
                    KeyboardButton(BotTextCommands.MySecurities.text)
                ),
//                listOf(KeyboardButton("Назад"))
            ),
            resizeKeyboard = true
        )

        is BotScreen.Error -> KeyboardReplyMarkup(
            KeyboardButton(BotTextCommands.Root.text),
            resizeKeyboard = true
        )

        is BotScreen.MySecurities -> KeyboardReplyMarkup(
            KeyboardButton(BotTextCommands.Root.text),
            resizeKeyboard = true
        )

        is BotScreen.SearchSecurities -> KeyboardReplyMarkup(
            KeyboardButton(BotTextCommands.Root.text),
            resizeKeyboard = true
        )


        else -> null
    }

    private fun screenParseMode(screen: BotScreen): ParseMode? = when (screen) {
        is BotScreen.Root -> ParseMode.MARKDOWN_V2
        else -> null
    }

    private fun onStop() {
        telegramBot.stopPolling()
        mainScope.cancel()
        botJob.cancel()
    }
}