package domain.navigation.useCase

import Resource
import domain.common.USER_NOT_FOUND_MESSAGE
import domain.local.model.User
import domain.local.repository.DatabaseRepository

class NavigateUserUseCase(private val repository: DatabaseRepository) {
    suspend operator fun invoke(id: Long, direction: String = String()): Resource<User> {
        val oldUser = repository.findUser(id) ?: return Resource.Error(USER_NOT_FOUND_MESSAGE)
        val path = oldUser.path + "/$direction"

        val newUser = oldUser.copy(path = path)
        val updatedUser = repository.updateUser(newUser) ?: return Resource.Error(USER_NOT_FOUND_MESSAGE)
        return Resource.Success(updatedUser)
    }
}