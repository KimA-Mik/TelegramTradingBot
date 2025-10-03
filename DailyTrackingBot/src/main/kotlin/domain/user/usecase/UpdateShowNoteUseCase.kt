package domain.user.usecase

import domain.user.model.User
import domain.user.repository.UserRepository
import kotlin.time.ExperimentalTime

class UpdateShowNoteUseCase(
    private val repository: UserRepository
) {
    @OptIn(ExperimentalTime::class)
    suspend operator fun invoke(user: User, newValue: Boolean) =
        repository.updateUser(user.copy(showNote = newValue, shouldNotify = true))
}