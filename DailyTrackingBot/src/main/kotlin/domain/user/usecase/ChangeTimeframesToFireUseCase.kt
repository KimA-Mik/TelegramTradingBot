package domain.user.usecase

import domain.user.model.User
import domain.user.repository.UserRepository

class ChangeTimeframesToFireUseCase(
    private val repository: UserRepository
) {
    suspend operator fun invoke(user: User, direction: Direction): User? {
        val current = user.timeframesToFire
        val newValue = when (direction) {
            Direction.INCREASE -> if (current >= TOTAL_TIMEFRAMES) 1 else current + 1
            Direction.DECREASE -> if (current < 2) TOTAL_TIMEFRAMES else current - 1
        }

        return repository.updateUser(user.copy(timeframesToFire = newValue))
    }

    enum class Direction {
        INCREASE,
        DECREASE
    }

    companion object {
        private const val TOTAL_TIMEFRAMES = 4
    }
}