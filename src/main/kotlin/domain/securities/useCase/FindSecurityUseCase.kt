package domain.securities.useCase

import Resource
import domain.repository.RequestService
import domain.securities.model.Security
import kotlinx.coroutines.flow.flow

class FindSecurityUseCase(private val requestService: RequestService) {
    suspend operator fun invoke(secName: String) = flow<Resource<Security>> {
        emit(Resource.Error(""))
    }
}