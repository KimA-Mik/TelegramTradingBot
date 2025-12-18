package domain.user.usecase

import domain.config.LocalConfigDataSource
import domain.user.mappers.type
import domain.user.model.TrackingSecurity
import domain.user.model.User
import domain.user.repository.UserRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import org.slf4j.LoggerFactory
import ru.kima.cacheserver.api.api.CacheServerApi
import ru.kima.cacheserver.api.schema.model.Security
import ru.kima.cacheserver.api.schema.model.requests.InstrumentsRequest

class SubscribeToDefaultSecuritiesUseCase(
    private val api: CacheServerApi,
    private val repository: UserRepository,
    private val localConfigDataSource: LocalConfigDataSource
) {
    private val logger = LoggerFactory.getLogger(this::class.java)
    suspend operator fun invoke(user: User): Res {
        val localConfigSecurities = localConfigDataSource.getLocalConfig().securities.toSet()
        if (localConfigSecurities.isEmpty()) return Res.NoDefaultSecurities
        val securities = getSecurities().getOrElse {
            logger.error("Failed to get securities from CacheServer", it)
            return Res.CacheServerError
        }

        val fullUser = repository.findFullUserById(user.id) ?: return Res.UnregisteredUser
        val fullUserSecurities = buildSet(fullUser.securities.size) {
            fullUser.securities.forEach {
                add(it.ticker)
            }
        }

        val requiredSecurities = securities
            .asSequence()
            .filter { localConfigSecurities.contains(it.ticker) }
            .filter { !fullUserSecurities.contains(it.ticker) }
            .map {
                TrackingSecurity.default(
                    ticker = it.ticker,
                    name = it.name,
                    uid = it.uid,
                    type = it.type
                )
            }
            .toList()

        val subscribingResult = repository.createTrackingSecurities(user, requiredSecurities)
        if (subscribingResult.isFailure) {
            logger.error(
                "Failed to subscribe user ${user.id} to default securities",
                subscribingResult.exceptionOrNull()
            )
            return Res.UnknownError
        }

        return Res.Success(requiredSecurities.size)
    }

    private suspend fun getSecurities(): Result<List<Security>> = runCatching {
        val scope = CoroutineScope(Dispatchers.IO)
        val sharesDeferred = scope.async {
            api.tradableShares(InstrumentsRequest.default)
        }
        val futuresDeferred = scope.async {
            api.tradableFutures(InstrumentsRequest.default)
        }
        val shares = sharesDeferred.await().getOrElse { return Result.failure(it) }
        val futures = futuresDeferred.await().getOrElse { return Result.failure(it) }
        shares + futures
    }

    sealed interface Res {
        data object NoDefaultSecurities : Res
        data class Success(val count: Int) : Res
        data object CacheServerError : Res
        data object UnknownError : Res
        data object UnregisteredUser : Res
    }
}