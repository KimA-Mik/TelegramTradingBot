package presentation.telegram.core

import com.github.kotlintelegrambot.entities.TelegramFile

sealed interface MediaType {
    data class Photo(
        val photo: TelegramFile,
        val protectContent: Boolean? = null,
    ) : MediaType
}
