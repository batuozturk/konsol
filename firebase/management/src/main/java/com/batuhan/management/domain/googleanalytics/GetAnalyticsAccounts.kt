package com.batuhan.management.domain.googleanalytics

import com.batuhan.core.data.repository.management.ManagementRepository
import javax.inject.Inject

class GetAnalyticsAccounts @Inject constructor(private val managementRepository: ManagementRepository) {

    operator fun invoke() = managementRepository.getAnalyticsAccounts()
}
