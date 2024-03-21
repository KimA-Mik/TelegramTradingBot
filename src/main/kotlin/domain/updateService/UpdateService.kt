package domain.updateService

import Resource
import domain.tinkoff.model.TinkoffFuture
import domain.tinkoff.model.TinkoffPrice
import domain.tinkoff.model.TinkoffShare
import domain.tinkoff.repository.TinkoffRepository
import domain.tinkoff.util.TinkoffFutureComparator
import domain.updateService.model.FollowedShare
import domain.updateService.model.UserWithFollowedShares
import domain.updateService.updates.Update
import domain.user.repository.DatabaseRepository
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import org.slf4j.LoggerFactory
import kotlin.random.Random

class UpdateService(
    private val database: DatabaseRepository,
    private val tinkoff: TinkoffRepository
) {
    private val _updates = MutableSharedFlow<Update>()
    val updates = _updates.asSharedFlow()

    private val job = SupervisorJob()
    private val scope = CoroutineScope(Dispatchers.Default + job)

    private val logger = LoggerFactory.getLogger(this::class.java)

    init {
        run()
    }

    private fun run() = scope.launch {
        while (isActive) {
            checkForUpdates()
            val delayTime = Random.nextFloat() * MILLIS_MINUTE * 2
            delay(MILLIS_MINUTE + delayTime.toLong())
        }
    }

    private suspend fun checkForUpdates() = supervisorScope {
        val usersWithFollowedShares = database.getUsersWithShares()
        if (usersWithFollowedShares.isEmpty()) return@supervisorScope

        val sharesTickers = mutableSetOf<String>()
        usersWithFollowedShares.forEach { userWithFollowedShares ->
            userWithFollowedShares.shares.forEach { share ->
                sharesTickers.add(share.ticker)
            }
        }

        val sharesPricesDeferred = async {
            getSharesPrices(sharesTickers)
        }

        val sharesToFutures = sharesTickers.associateBy({ it }, {
            val temp = TinkoffShare(ticker = it)
            val resource = tinkoff.getSecurityFutures(temp)
            resource
                .data
                ?.sortedWith(TinkoffFutureComparator)
                ?.take(4)
                ?: emptyList()
        })

        val futuresList = sharesToFutures.flatMap { it.value }
        val futuresPricesDeferred = async {
            getFuturesPrices(futuresList)
        }

        val sharesPrices = when (val res = sharesPricesDeferred.await()) {
            is Resource.Success -> res.data!!.associateBy { it.ticker }
            is Resource.Error -> {
                logger.info("Unable to load futures prices: ${res.message}")
                return@supervisorScope
            }
        }

        val futuresPrices = when (val res = futuresPricesDeferred.await()) {
            is Resource.Success -> res.data!!.associateBy { it.ticker }
            is Resource.Error -> {
                logger.info("Unable to load futures prices: ${res.message}")
                return@supervisorScope
            }
        }

        usersWithFollowedShares.forEach { user ->
            handleUser(
                user = user,
                sharesToFutures = sharesToFutures,
                sharesPrices = sharesPrices,
                futuresPrices = futuresPrices
            )
        }
    }

    private suspend fun handleUser(
        user: UserWithFollowedShares,
        sharesToFutures: Map<String, List<TinkoffFuture>>,
        sharesPrices: Map<String, TinkoffPrice>,
        futuresPrices: Map<String, TinkoffPrice>
    ) = coroutineScope {
        val handled = emptyList<FollowedShare>()
        user.shares.forEach { share ->
            val sharePrice = sharesPrices[share.ticker] ?: return@forEach
            val futures = sharesToFutures[share.ticker] ?: return@forEach
            if (futures.isEmpty()) return@forEach


        }
    }

    private suspend fun getSharesPrices(sharesTickers: Iterable<String>): Resource<List<TinkoffPrice>> {
        val shares = sharesTickers.mapNotNull {
            tinkoff.getSecurity(it).data
        }

        return try {
            tinkoff.getSharesPrice(shares)
        } catch (e: Exception) {
            Resource.Error(e.message)
        }
    }

    private suspend fun getFuturesPrices(futuresList: List<TinkoffFuture>): Resource<List<TinkoffPrice>> {
        return try {
            tinkoff.getFuturesPrices(futuresList)
        } catch (e: Exception) {
            Resource.Error(e.message)
        }
    }

    companion object {
        private const val MILLIS_MINUTE = 10000L
    }
}