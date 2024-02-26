package presentation.telegram.model

data class Message(val id: Long, val text: String, val buttonsMarkup: ButtonsMarkup? = null)