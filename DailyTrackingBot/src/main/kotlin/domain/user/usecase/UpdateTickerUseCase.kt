package domain.user.usecase

import domain.user.model.User
import domain.user.repository.UserRepository
import kotlin.time.ExperimentalTime

class UpdateTickerUseCase(
    private val repository: UserRepository,
) {
    @OptIn(ExperimentalTime::class)
    suspend operator fun invoke(user: User, ticker: String): User {
        var result = user
//        val newPath = if (user.securityConfigureSequence) {
//            val destinations = user.path.split(PATH_SEPARATOR)
//            val currentDestination =
//                StartSecurityConfigureSequenceUseCase.actionsSequence.indexOfLast { it.destination == destinations.last() }
//            if (currentDestination < 0) {
//                result = repository.updateUser(user.copy(securityConfigureSequence = false)) ?: user
//                user.path
//            } else if (currentDestination == StartSecurityConfigureSequenceUseCase.actionsSequence.lastIndex) {
//                result = repository.updateUser(user.copy(securityConfigureSequence = false)) ?: user
//                user.path.split(PATH_SEPARATOR).dropLast(1)
//                    .joinToString(PATH_SEPARATOR.toString())
//            } else {
//                user.path.split(PATH_SEPARATOR).dropLast(1)
//                    .joinToString(PATH_SEPARATOR.toString()) +
//                        PATH_SEPARATOR + StartSecurityConfigureSequenceUseCase.actionsSequence[currentDestination + 1].destination
//            }
//        } else {
//            user.path
//        }
        return repository.updateUser(result.copy(ticker = ticker /*,path = newPath*/)) ?: user
    }
}