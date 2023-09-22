package com.batuhan.management.domain.googlecloud

import com.batuhan.core.data.repository.management.ManagementRepository
import javax.inject.Inject

class GetBillingAccounts @Inject constructor(private val managementRepository: ManagementRepository) {

    operator fun invoke() = managementRepository.getBillingAccounts()
}