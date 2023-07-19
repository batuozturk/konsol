package com.batuhan.management.domain.googleanalytics

import com.batuhan.management.data.repository.ManagementRepository
import javax.inject.Inject

class GetAnalyticsAccounts @Inject constructor(private val managementRepository: ManagementRepository) {

    operator fun invoke() = managementRepository.getAnalyticsAccounts()
}
