package presentation.common

object MarkdownUtil {
    fun inlineUrl(text: String, url: String) = "[$text]($url)"
}