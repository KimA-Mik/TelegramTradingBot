package domain.user.usecase

import domain.common.PATH_SEPARATOR
import domain.user.exceptions.UserException
import domain.user.model.User
import domain.user.repository.UserRepository
import kotlin.time.ExperimentalTime

class NavigateUserUseCase(
    private val repository: UserRepository
) {
    @OptIn(ExperimentalTime::class)
    suspend operator fun invoke(user: User, direction: String = ""): Result<User> {
        val path = user.path + PATH_SEPARATOR + direction

        val newUser = user.copy(path = path)
        val updatedUser = repository.updateUser(newUser)
            ?: return Result.failure(UserException.UserNotFoundException())
        return Result.success(updatedUser)
    }

    @OptIn(ExperimentalTime::class)
    suspend fun absolute(user: User, vararg path: String): Result<User> {
        var path = path.joinToString(PATH_SEPARATOR.toString())
        if (path.isNotBlank()) path = PATH_SEPARATOR + path

        val newUser = user.copy(path = path)
        val updatedUser = repository.updateUser(newUser)
            ?: return Result.failure(UserException.UserNotFoundException())
        return Result.success(updatedUser)
    }
}