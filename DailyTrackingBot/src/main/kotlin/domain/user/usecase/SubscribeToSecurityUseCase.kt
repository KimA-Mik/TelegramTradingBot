package domain.user.usecase

import domain.tinkoff.usecase.FindSecurityUseCase
import domain.user.mappers.type
import domain.user.model.TrackingSecurity
import domain.user.model.User
import domain.user.repository.UserRepository
import org.slf4j.LoggerFactory

class SubscribeToSecurityUseCase(
    private val repository: UserRepository,
    private val findSecurity: FindSecurityUseCase
) {
    private val logger = LoggerFactory.getLogger(this::class.java)
    suspend operator fun invoke(user: User, ticker: String): FindSecurityUseCase.Result {
        val securityResult = findSecurity(user.id, ticker)
        if (securityResult !is FindSecurityUseCase.Result.Success) return securityResult
        if (securityResult.subscribed) return securityResult

        val subscribingResult = repository.createTrackingSecurity(
            user, TrackingSecurity.default(
                ticker = securityResult.security.ticker,
                uid = securityResult.security.uid,
                name = securityResult.security.name,
                type = securityResult.security.type,
                targetPrice = securityResult.price ?: 0.0,
            )
        )

        if (subscribingResult.isSuccess) {
            return securityResult.copy(subscribed = true)
        } else {
            logger.error("Failed to subscribe tracking security for user ${user.id} because ${subscribingResult.exceptionOrNull()}")
            return FindSecurityUseCase.Result.NotFound
        }
    }
}