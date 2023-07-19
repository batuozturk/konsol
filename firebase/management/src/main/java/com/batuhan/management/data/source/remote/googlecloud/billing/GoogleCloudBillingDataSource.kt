package com.batuhan.management.data.source.remote.googlecloud.billing

import com.batuhan.management.data.model.UpdateBillingInfoRequest
import javax.inject.Inject

class GoogleCloudBillingDataSource @Inject constructor(private val billingService: GoogleCloudBillingService) {

    suspend fun getBillingInfo(projectId: String) = billingService.getBillingInfo(projectId)

    suspend fun updateBillingInfo(projectId: String, updateBillingInfoRequest: UpdateBillingInfoRequest) =
        billingService.updateBillingInfo(projectId, updateBillingInfoRequest)
}
