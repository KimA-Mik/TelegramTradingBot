package presentation.util

object MarkdownUtil {
    fun inlineUrl(text: String, url: String) = "[$text]($url)"
}