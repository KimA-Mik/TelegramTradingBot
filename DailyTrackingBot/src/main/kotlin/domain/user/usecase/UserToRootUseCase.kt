package domain.user.usecase

import domain.user.exceptions.UserException
import domain.user.model.User
import domain.user.repository.UserRepository
import kotlin.time.ExperimentalTime

class UserToRootUseCase(private val repository: UserRepository) {
    @OptIn(ExperimentalTime::class)
    suspend operator fun invoke(user: User): Result<User> {
        val newUser = user.copy(path = String())
        val updatedUser = repository.updateUser(newUser)
            ?: return Result.failure(UserException.UserNotFoundException())
        return Result.success(updatedUser)
    }
}