package domain.user.useCase

import domain.user.repository.DatabaseRepository

class ResetNotificationUseCase(
    private val database: DatabaseRepository
) {
    suspend operator fun invoke(userId: Long, shareTicker: String): Boolean {
        val shares = database.getUserShares(userId)
        val share = shares.find { it.ticker == shareTicker }
            ?: return false

        if (!share.notified) return false

        val updatedShare = share.copy(notified = false)
        database.updateUserShares(listOf(updatedShare))

        return true
    }
}