package br.com.hospitalmaps.domain.usecase

import br.com.hospitalmaps.data.repository.HospitalRepository
import kotlinx.coroutines.flow.first

class GetNearestHospitalUseCase(
    private val hospitalRepository: HospitalRepository
) {
    suspend operator fun invoke() = hospitalRepository.getNearbyHospitals().first().first()
}