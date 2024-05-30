package com.batuhan.core.data.source.remote.management.googlecloud.billing

import com.batuhan.core.data.model.management.UpdateBillingInfoRequest
import javax.inject.Inject

class GoogleCloudBillingDataSource @Inject constructor(private val billingService: GoogleCloudBillingService) {

    suspend fun getBillingInfo(projectId: String) = billingService.getBillingInfo(projectId)

    suspend fun updateBillingInfo(projectId: String, updateBillingInfoRequest: UpdateBillingInfoRequest) =
        billingService.updateBillingInfo(projectId, updateBillingInfoRequest)
}
