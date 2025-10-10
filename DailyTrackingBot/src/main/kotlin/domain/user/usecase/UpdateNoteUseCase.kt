package domain.user.usecase

import domain.user.model.TrackingSecurity
import domain.user.model.User
import domain.user.repository.UserRepository

class UpdateNoteUseCase(
    private val repository: UserRepository
) {
    suspend operator fun invoke(user: User, ticker: String, text: String?): TrackingSecurity? {
        val fullUser = repository.findFullUserById(user.id) ?: return null
        val security = fullUser.securities.find { it.ticker == ticker } ?: return null
        return repository.updateTrackingSecurity(
            security.copy(note = text, shouldNotify = true, shouldNotifyRsi = true)
        ).getOrNull()
    }
}