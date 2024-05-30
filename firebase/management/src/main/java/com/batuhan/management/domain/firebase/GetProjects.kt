package com.batuhan.management.domain.firebase

import com.batuhan.core.data.repository.management.ManagementRepository
import javax.inject.Inject

class GetProjects @Inject constructor(private val repository: ManagementRepository) {

    operator fun invoke() = repository.getProjects()
}
