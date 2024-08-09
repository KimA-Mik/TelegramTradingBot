package domain.user.useCase.indicators.rsi

import domain.user.model.User
import domain.user.repository.DatabaseRepository
import domain.user.useCase.indicators.result.SwitchIndicatorDefaultResult

class SwitchRsiDefaultUseCase(private val repository: DatabaseRepository) {
    suspend operator fun invoke(user: User): SwitchIndicatorDefaultResult {
        val newUser = user.copy(defaultRsiNotifications = !user.defaultRsiNotifications)
        val res = repository.updateUser(newUser)

        res?.let {
            return SwitchIndicatorDefaultResult.Success(it)
        }
        return SwitchIndicatorDefaultResult.Error
    }
}