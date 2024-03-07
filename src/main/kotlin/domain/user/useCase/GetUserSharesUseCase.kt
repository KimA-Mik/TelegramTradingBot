package domain.user.useCase

import domain.user.model.UserShare
import domain.user.repository.DatabaseRepository

class GetUserSharesUseCase(private val repository: DatabaseRepository) {
    suspend operator fun invoke(userId: Long, page: Int): GetUserSharesResult {
        val shares = repository.getUserShares(userId)
        if (shares.isEmpty()) return GetUserSharesResult.NotFound

        val totalPages = (shares.size - 1) / PAGE_SIZE + 1
        var resultPage = page
        if (resultPage < 1) resultPage = totalPages
        if (resultPage > totalPages) resultPage = 1

        return GetUserSharesResult.Success(
            shares = shares
                .drop(PAGE_SIZE * (resultPage - 1))
                .take(PAGE_SIZE),
            page = resultPage,
            totalPages = totalPages
        )
    }

    sealed interface GetUserSharesResult {
        data class Success(
            val shares: List<UserShare>,
            val page: Int,
            val pageSize: Int = PAGE_SIZE,
            val totalPages: Int
        ) : GetUserSharesResult

        data object NotFound : GetUserSharesResult
    }

    companion object {
        private const val PAGE_SIZE = 5
    }
}