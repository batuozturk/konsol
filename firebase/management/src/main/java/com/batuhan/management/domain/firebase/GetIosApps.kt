package com.batuhan.management.domain.firebase

import com.batuhan.management.data.repository.ManagementRepository
import javax.inject.Inject

class GetIosApps @Inject constructor(private val managementRepository: ManagementRepository) {

    data class Params(val projectId: String)

    operator fun invoke(params: Params) = managementRepository.getIosApps(params.projectId)
}
