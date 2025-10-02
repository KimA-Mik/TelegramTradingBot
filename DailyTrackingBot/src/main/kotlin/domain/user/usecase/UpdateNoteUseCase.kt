package domain.user.usecase

import domain.user.model.User
import domain.user.repository.UserRepository
import kotlin.time.ExperimentalTime

class UpdateNoteUseCase(
    private val repository: UserRepository
) {
    @OptIn(ExperimentalTime::class)
    suspend operator fun invoke(user: User, text: String?) = repository.updateUser(user.copy(note = text))
}