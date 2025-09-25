package domain.user.useCase

import Resource
import domain.common.USER_NOT_FOUND_MESSAGE
import domain.user.model.User
import domain.user.repository.DatabaseRepository

class FindUserUseCase(private val repository: DatabaseRepository) {
    suspend operator fun invoke(id: Long): Resource<User> {
        val user = repository.findUser(id) ?: return Resource.Error(USER_NOT_FOUND_MESSAGE)
        return Resource.Success(user)
    }
}