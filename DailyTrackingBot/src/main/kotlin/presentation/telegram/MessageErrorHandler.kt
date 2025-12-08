package presentation.telegram

import com.github.kotlintelegrambot.types.TelegramBotResult
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import org.slf4j.LoggerFactory
import presentation.telegram.core.UiError
import presentation.telegram.core.screen.BotScreen
import presentation.telegram.core.screen.ErrorScreen
import presentation.telegram.security.edit.screen.SecurityScreen
import presentation.telegram.security.update.SecurityAlertScreen

class MessageErrorHandler {
    private val logger = LoggerFactory.getLogger(this::class.java)
    private val _outMessages = MutableSharedFlow<BotScreen>()
    val outMessages = _outMessages.asSharedFlow()

    suspend fun handleError(screen: BotScreen, error: TelegramBotResult.Error) {
        when (error) {
            is TelegramBotResult.Error.HttpError -> handleHttpError(screen, error)
            is TelegramBotResult.Error.InvalidResponse -> handleInvalidResponse(screen, error)
            is TelegramBotResult.Error.TelegramApi -> handleTelegramApiError(screen, error)
            is TelegramBotResult.Error.Unknown -> handleUnknownError(screen, error)
        }
    }

    private fun handleHttpError(
        screen: BotScreen,
        error: TelegramBotResult.Error.HttpError
    ) = logger.error("HTTP error: $error for screen: $screen")


    private fun handleInvalidResponse(
        screen: BotScreen,
        error: TelegramBotResult.Error.InvalidResponse
    ) = logger.error("Invalid response error: $error for screen: $screen")


    private suspend fun handleTelegramApiError(
        screen: BotScreen,
        error: TelegramBotResult.Error.TelegramApi
    ) {
        if (screen.shouldFireError()) {
            _outMessages.emit(ErrorScreen(screen.id, UiError.TelegramApiError))
            return
        }

        // Specific handling for markdown entity errors
        if (error.errorCode == 400 &&
            (error.description.contains(CANT_PARSE_ENTITIES) || error.description.contains(ENTITIES_TOO_LONG))
        ) {
            when (screen) {
                is SecurityAlertScreen -> {
                    screen.fixNoteMarkdown()
                    _outMessages.emit(screen)
                }

                is SecurityScreen -> {
                    screen.fixNoteMarkdown()
                    _outMessages.emit(screen)
                }

                else -> _outMessages.emit(ErrorScreen(screen.id, UiError.TelegramApiError))
            }
        } else {
            logger.error("Telegram API error what shouldn't fire and couldn't be handled: $error for screen: $screen")
            _outMessages.emit(ErrorScreen(screen.id, UiError.TelegramApiError))
        }
    }

    private fun handleUnknownError(
        screen: BotScreen,
        error: TelegramBotResult.Error.Unknown
    ) = logger.error("Unknown error: $error for screen: $screen")

    companion object {
        private const val CANT_PARSE_ENTITIES = "Bad Request: can't parse entities"
        private const val ENTITIES_TOO_LONG = "You provided too many styled message entities"
    }
}