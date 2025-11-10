package ru.kima.telegrambot.common.util

object MarkdownUtil {
    fun inlineUrl(text: String, url: String) = "[$text]($url)"
}