package domain.useCase

import domain.local.model.User
import domain.local.repository.DatabaseRepository

class NavigateUserUseCase(private val repository: DatabaseRepository) {
    suspend operator fun invoke(id: Long, direction: String = String(), pop: Boolean = false): NavResult {
        val oldUser = repository.findUser(id) ?: return NavResult.NoUser
        val path = if (pop) {
            oldUser.path
                .split('/')
                .dropLast(1)
                .joinToString("/")
        } else {
            oldUser.path + "/$direction"
        }


        val newUser = oldUser.copy(path = path)
        val updatedUser = repository.updateUser(newUser)

        return if (updatedUser == null) {
            NavResult.NoUser
        } else {
            NavResult.Success(updatedUser)
        }

        return NavResult.Unreachable(direction)
    }

    sealed interface NavResult {
        data object NoUser : NavResult
        data class Unreachable(val path: String) : NavResult
        data class Success(val user: User) : NavResult
    }
}