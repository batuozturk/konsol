package com.batuhan.core.domain.management

import com.batuhan.core.data.repository.management.ManagementRepository
import javax.inject.Inject

class GetAvailableLocations @Inject constructor(private val managementRepository: ManagementRepository) {

    data class Params(val projectName: String)

    operator fun invoke(params: Params) =
        managementRepository.getAvailableLocations(params.projectName)
}