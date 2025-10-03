package domain.user.usecase

import domain.common.parseToDouble
import domain.user.model.User
import domain.user.repository.UserRepository
import kotlin.time.ExperimentalTime

class UpdatePercentUseCase(
    private val repository: UserRepository
) {
    @OptIn(ExperimentalTime::class)
    suspend operator fun invoke(user: User, inputNumber: String): Result<User> = runCatching {
        val percent = inputNumber.parseToDouble()
        repository.updateUser(user.copy(targetDeviation = percent, shouldNotify = true))!!
    }
}