package domain.user.navigation.useCase

import Resource
import domain.common.PATH_SEPARATOR
import domain.common.USER_NOT_FOUND_MESSAGE
import domain.user.model.User
import domain.user.repository.DatabaseRepository

class NavigateUserUseCase(private val repository: DatabaseRepository) {
    suspend operator fun invoke(user: User, direction: String = String()): Resource<User> {
        val path = user.path + PATH_SEPARATOR + direction

        val newUser = user.copy(path = path)
        val updatedUser = repository.updateUser(newUser) ?: return Resource.Error(USER_NOT_FOUND_MESSAGE)
        return Resource.Success(updatedUser)
    }
}