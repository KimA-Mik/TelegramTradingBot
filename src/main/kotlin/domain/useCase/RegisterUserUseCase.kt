package domain.useCase

import Resource
import domain.local.repository.DatabaseRepository

class RegisterUserUseCase(private val repository: DatabaseRepository) {
    suspend operator fun invoke(id: Long): Resource<Unit> {
        val existedUser = repository.findUser(id)
        if (existedUser != null) {
            return Resource.Error("Пользователь уже зарегестрирован")
        }

        repository.registerUser(id)
        return Resource.Success(Unit)
    }
}