package domain.moex.securities.useCase

import Resource
import domain.moex.repository.MoexRepository
import domain.moex.securities.model.Security

class FindSecurityUseCase(private val moexRepository: MoexRepository) {
    suspend operator fun invoke(secName: String): Resource<Security> {
        println("Looking for ${secName}")
        return moexRepository.getSecurity(secName)
    }
}