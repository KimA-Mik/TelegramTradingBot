package presentation.telegram

import com.github.kotlintelegrambot.Bot
import com.github.kotlintelegrambot.bot
import com.github.kotlintelegrambot.dispatch
import com.github.kotlintelegrambot.dispatcher.callbackQuery
import com.github.kotlintelegrambot.dispatcher.command
import com.github.kotlintelegrambot.dispatcher.telegramError
import com.github.kotlintelegrambot.dispatcher.text
import com.github.kotlintelegrambot.entities.*
import com.github.kotlintelegrambot.entities.keyboard.InlineKeyboardButton
import com.github.kotlintelegrambot.entities.keyboard.KeyboardButton
import domain.tinkoff.model.TinkoffPrice
import kotlinx.coroutines.*
import presentation.telegram.model.SecuritySearchResultData

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
                    callbackData = "followSecurity",
                    callbackAnswerText = "Я так пока не умею.",
                    callbackAnswerShowAlert = true,
                ) {
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
        is BotScreen.MySecurities -> "Пока тут не на что смотреть"
        is BotScreen.SearchSecurities -> "Введите тикер ценной бумаги"
        is BotScreen.SecuritySearchResult -> getSearchResultText(screen.result)
        is BotScreen.SecurityNotFound -> "Акция ${screen.name} не найдена"
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

        is BotScreen.SecuritySearchResult -> InlineKeyboardMarkup.create(
            listOf(
                InlineKeyboardButton.CallbackData("Отслеживать", "followSecurity")
            )
        )


        else -> null
    }

    private fun screenParseMode(screen: BotScreen): ParseMode? = when (screen) {
        is BotScreen.Root -> ParseMode.MARKDOWN_V2
        else -> null
    }

    private fun getSearchResultText(searchResult: SecuritySearchResultData): String {
        var result =
            "Акция ${searchResult.security.share.ticker} - ${searchResult.security.share.name}: ${searchResult.sharePrice.price}$ROUBLE"
        searchResult.security.futures.forEachIndexed { index, future ->
            val price = searchResult.futuresPrices.getOrElse(index) { TinkoffPrice() }
            result += "\nФьючерс ${future.ticker} - ${future.name}: ${price.price}$ROUBLE"
        }

        return result
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