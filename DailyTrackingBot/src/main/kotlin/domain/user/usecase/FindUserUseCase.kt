package domain.user.usecase

import domain.user.exceptions.UserException
import domain.user.model.User
import domain.user.repository.UserRepository

class FindUserUseCase(
    private val repository: UserRepository
) {
    suspend operator fun invoke(id: Long): Result<User> {
        repository.findUserById(id)?.let {
            return Result.success(it)
        }
        return Result.failure(UserException.UserNotFoundException())
    }
}