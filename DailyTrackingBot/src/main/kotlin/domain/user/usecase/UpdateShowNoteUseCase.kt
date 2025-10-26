package domain.user.usecase

import domain.user.model.TrackingSecurity
import domain.user.model.User
import domain.user.repository.UserRepository

class UpdateShowNoteUseCase(
    private val repository: UserRepository
) {
    suspend operator fun invoke(user: User, ticker: String, newValue: Boolean): TrackingSecurity? {
        val fullUser = repository.findFullUserById(user.id) ?: return null
        val security = fullUser.securities.find { it.ticker == ticker } ?: return null
        return repository.updateTrackingSecurity(
            security.copy(showNote = newValue, shouldNotify = true, shouldNotifyRsi = true, shouldNotifyBb = true)
        ).getOrNull()
    }
}