package domain.user.usecase

import domain.common.PATH_SEPARATOR
import domain.user.model.User
import domain.user.repository.UserRepository
import presentation.telegram.core.NavigationRoot
import kotlin.time.ExperimentalTime

class StartSecurityConfigureSequenceUseCase(
    private val repository: UserRepository
) {
    @OptIn(ExperimentalTime::class)
    suspend operator fun invoke(user: User): User? = repository.updateUser(
        user.copy(
            path = user.path + PATH_SEPARATOR + actionsSequence.first().destination,
            securityConfigureSequence = true
        )
    )

    companion object {
        val actionsSequence = listOf(
            NavigationRoot.Security.EditTicker,
            NavigationRoot.Security.EditPrice,
            NavigationRoot.Security.EditPercentage
        )
    }
}