package domain.user.usecase

import domain.user.exceptions.UserException
import domain.user.repository.UserRepository

class RegisterUserUseCase(
    private val repository: UserRepository
) {
    suspend operator fun invoke(id: Long): Result<Unit> {
        val existedUser = repository.findUserById(id)
        if (existedUser != null) {
            return Result.failure(UserException.UserAlreadyRegistered(id))
        }

        repository.registerUser(id)
        return Result.success(Unit)
    }
}