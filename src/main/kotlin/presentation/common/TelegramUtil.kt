package presentation.common

object TelegramUtil {
    fun markdownInlineUrl(text: String, url: String) = "[$text]($url)"
}