package domain.user.usecase

import domain.user.model.User
import domain.user.repository.UserRepository
import kotlin.time.ExperimentalTime

class ToggleSrsiAlertUseCase(
    private val repository: UserRepository
) {
    @OptIn(ExperimentalTime::class)
    suspend operator fun invoke(user: User, newVal: Boolean) = repository.updateUser(user.copy(enableSrsi = newVal))
}