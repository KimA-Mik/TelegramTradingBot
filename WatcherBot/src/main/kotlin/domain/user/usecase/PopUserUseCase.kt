package domain.user.usecase

import domain.common.PATH_SEPARATOR
import domain.user.exceptions.UserException
import domain.user.model.User
import domain.user.repository.UserRepository
import kotlin.time.ExperimentalTime

class PopUserUseCase(
    private val repository: UserRepository
) {
    @OptIn(ExperimentalTime::class)
    suspend operator fun invoke(user: User): Result<User> {
        val path = user.path
            .split(PATH_SEPARATOR)
            .dropLast(1)
            .joinToString("$PATH_SEPARATOR")

        val newUser = user.copy(path = path)
        val updatedUser = repository.updateUser(newUser)
            ?: return Result.failure(UserException.UserNotFoundException())
        return Result.success(updatedUser)
    }
}