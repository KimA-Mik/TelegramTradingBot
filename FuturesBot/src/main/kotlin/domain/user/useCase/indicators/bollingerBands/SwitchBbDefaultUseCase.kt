package domain.user.useCase.indicators.bollingerBands

import domain.user.model.User
import domain.user.repository.DatabaseRepository
import domain.user.useCase.indicators.result.SwitchIndicatorDefaultResult

class SwitchBbDefaultUseCase(private val repository: DatabaseRepository) {
    suspend operator fun invoke(user: User): SwitchIndicatorDefaultResult {
        val newUser = user.copy(defaultBbNotifications = !user.defaultBbNotifications)
        val res = repository.updateUser(newUser)

        res?.let {
            return SwitchIndicatorDefaultResult.Success(it)
        }
        return SwitchIndicatorDefaultResult.Error
    }
}