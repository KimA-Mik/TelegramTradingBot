package domain.user.exceptions

sealed class UserException : Exception() {
    data class UserAlreadyRegistered(val id: Long) : UserException() {
        override val message = "Пользователь уже зарегистрирован"
    }

    class UserNotFoundException : UserException()
}