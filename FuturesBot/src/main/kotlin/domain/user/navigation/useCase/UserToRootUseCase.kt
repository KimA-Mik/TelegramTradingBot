package domain.user.navigation.useCase

import Resource
import domain.common.USER_NOT_FOUND_MESSAGE
import domain.user.model.User
import domain.user.repository.DatabaseRepository

class UserToRootUseCase(private val repository: DatabaseRepository) {
    suspend operator fun invoke(user: User): Resource<User> {
        val newUser = user.copy(path = String())
        val updatedUser = repository.updateUser(newUser) ?: return Resource.Error(USER_NOT_FOUND_MESSAGE)
        return Resource.Success(updatedUser)
    }
}