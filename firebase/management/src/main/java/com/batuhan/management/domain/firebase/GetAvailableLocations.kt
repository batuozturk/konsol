package com.batuhan.management.domain.firebase

import com.batuhan.management.data.repository.ManagementRepository
import javax.inject.Inject

class GetAvailableLocations @Inject constructor(private val managementRepository: ManagementRepository) {

    data class Params(val projectName: String)

    operator fun invoke(params: Params) =
        managementRepository.getAvailableLocations(params.projectName)
}
