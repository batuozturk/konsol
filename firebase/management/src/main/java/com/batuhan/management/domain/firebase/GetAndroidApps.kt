package com.batuhan.management.domain.firebase

import com.batuhan.core.data.repository.management.ManagementRepository
import javax.inject.Inject

class GetAndroidApps @Inject constructor(private val managementRepository: ManagementRepository) {

    data class Params(val projectId: String)

    operator fun invoke(params: Params) = managementRepository.getAndroidApps(params.projectId)

}