package domain.user.usecase

import domain.common.parseToDouble
import domain.user.model.User
import domain.user.repository.UserRepository
import kotlin.time.ExperimentalTime

class UpdateExpectedPriceUseCase(
    private val repository: UserRepository
) {
    @OptIn(ExperimentalTime::class)
    suspend operator fun invoke(user: User, inputNumber: String): Result<User> = runCatching {
        val number = inputNumber.parseToDouble()
        repository.updateUser(user.copy(targetPrice = number, shouldNotify = true))!!
    }
}