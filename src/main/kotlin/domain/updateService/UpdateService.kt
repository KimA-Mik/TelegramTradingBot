package domain.updateService

import domain.tinkoff.repository.TinkoffRepository
import domain.updateService.updates.Update
import domain.user.repository.DatabaseRepository
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlin.random.Random

class UpdateService(
    private val database: DatabaseRepository,
    private val tinkoff: TinkoffRepository
) {
    private val _updates = MutableSharedFlow<Update>()
    val updates = _updates.asSharedFlow()

    private val job = SupervisorJob()
    private val scope = CoroutineScope(Dispatchers.Default + job)

    init {
        run()
    }

    private fun run() = scope.launch {
        while (isActive) {
            checkForUpdates()
            val delayTime = Random.nextFloat() * 2000
            delay(1000L + delayTime.toLong())
        }
    }

    private suspend fun checkForUpdates() {
        val usersWithFollowedShares = database.getUsersWithShares()
        if (usersWithFollowedShares.isEmpty()) return


    }
}