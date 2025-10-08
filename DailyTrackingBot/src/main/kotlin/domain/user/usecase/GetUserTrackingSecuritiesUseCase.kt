package domain.user.usecase

import domain.user.model.TrackingSecurity
import domain.user.repository.UserRepository

class GetUserTrackingSecuritiesUseCase(
    private val repository: UserRepository,
) {
    suspend operator fun invoke(
        userId: Long, page: Int,
        pageSize: Int = DEFAULT_PAGE_SIZE
    ): GetUserSharesResult {
        val securities = repository.findFullUserById(userId)?.securities
        if (securities == null || securities.isEmpty()) return GetUserSharesResult.NotFound

        val totalPages = (securities.size - 1) / pageSize + 1
        var resultPage = page
        if (resultPage < 1) resultPage = totalPages
        if (resultPage > totalPages) resultPage = 1

        return GetUserSharesResult.Success(
            securities = securities
                .drop(pageSize * (resultPage - 1))
                .take(pageSize),
            page = resultPage,
            pageSize = pageSize,
            totalPages = totalPages
        )
    }

    sealed interface GetUserSharesResult {
        data class Success(
            val securities: List<TrackingSecurity>,
            val page: Int,
            val pageSize: Int,
            val totalPages: Int
        ) : GetUserSharesResult

        data object NotFound : GetUserSharesResult
    }

    companion object {
        private const val DEFAULT_PAGE_SIZE = 5
    }
}