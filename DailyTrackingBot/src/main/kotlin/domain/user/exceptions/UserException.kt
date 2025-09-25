package domain.user.exceptions

sealed class UserException : Exception() {
    data class UserAlreadyRegistered(val id: Long) : UserException()
    class UserNotFoundException : UserException()
}