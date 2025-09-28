package domain.tinkoff.usecase

import ru.kima.cacheserver.api.api.CacheServerApi
import ru.kima.cacheserver.api.schema.model.requests.FindSecurityResponse

class GetSecurityUseCase(private val api: CacheServerApi) {
    suspend operator fun invoke(ticker: String): FindSecurityResponse = api.findSecurity(ticker)
}