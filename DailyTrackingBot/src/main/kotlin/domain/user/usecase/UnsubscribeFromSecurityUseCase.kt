package domain.user.usecase

import domain.tinkoff.usecase.FindSecurityUseCase
import domain.user.model.User
import domain.user.repository.UserRepository

class UnsubscribeFromSecurityUseCase(
    private val repository: UserRepository,
    private val findSecurity: FindSecurityUseCase
) {
    //TODO: Depending on `findSecurity` result is cringe, need to refactor
    suspend operator fun invoke(user: User, ticker: String): FindSecurityUseCase.Result {
        val securityResult = findSecurity(user.id, ticker)
        if (securityResult !is FindSecurityUseCase.Result.Success) return securityResult
        if (!securityResult.subscribed) return securityResult

        val fullUser = repository.findFullUserById(user.id) ?: return FindSecurityUseCase.Result.NotFound
        val trackingSecurity = fullUser.securities.firstOrNull { it.ticker == ticker }
            ?: return FindSecurityUseCase.Result.NotFound

        repository.deleteTrackingSecurity(trackingSecurity.id)
        return securityResult.copy(subscribed = false)
    }
}