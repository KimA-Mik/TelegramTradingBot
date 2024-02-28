package domain.user.navigation.useCase

import Resource
import domain.common.PATH_SEPARATOR
import domain.common.USER_NOT_FOUND_MESSAGE
import domain.user.model.User
import domain.user.repository.DatabaseRepository

class PopUserUseCase(private val repository: DatabaseRepository) {
    suspend operator fun invoke(user: User): Resource<User> {
        val path = user.path
            .split(PATH_SEPARATOR)
            .dropLast(1)
            .joinToString("$PATH_SEPARATOR")

        val newUser = user.copy(path = path)
        val updatedUser = repository.updateUser(newUser) ?: return Resource.Error(USER_NOT_FOUND_MESSAGE)
        return Resource.Success(updatedUser)
    }
}