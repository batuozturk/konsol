package com.batuhan.management.domain.firebase

import com.batuhan.management.data.repository.ManagementRepository
import javax.inject.Inject

class GetAvailableProjects @Inject constructor(private val managementRepository: ManagementRepository) {

    operator fun invoke() = managementRepository.getAvailableProjects()
}
