package ru.kima.cacheserver.api.schema.model.requests

private typealias SchemaShare = ru.kima.cacheserver.api.schema.model.Share
private typealias SchemaFuture = ru.kima.cacheserver.api.schema.model.Future

sealed interface FindSecurityResponse {
    data class Share(val share: SchemaShare) : FindSecurityResponse
    data class Future(val future: SchemaFuture) : FindSecurityResponse
    data object NotFound : FindSecurityResponse
    data class UnknownError(val exception: Exception) : FindSecurityResponse
}