package domain.securities.useCase

import Resource
import domain.repository.MoexRepository
import domain.securities.model.Security

class FindSecurityUseCase(private val moexRepository: MoexRepository) {
    suspend operator fun invoke(secName: String): Resource<Security> {
        println("Looking for ${secName}")
        return moexRepository.getSecurity(secName)
    }
}