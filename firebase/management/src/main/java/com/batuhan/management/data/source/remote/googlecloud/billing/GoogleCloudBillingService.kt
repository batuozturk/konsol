package com.batuhan.management.data.source.remote.googlecloud.billing

import com.batuhan.management.data.model.BillingAccountResponse
import com.batuhan.management.data.model.ProjectBillingInfo
import com.batuhan.management.data.model.UpdateBillingInfoRequest
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface GoogleCloudBillingService {

    companion object {
        private const val PATH = "v1"
    }

    @GET("$PATH/projects/{projectId}/billingInfo")
    suspend fun getBillingInfo(@Path("projectId") projectId: String): ProjectBillingInfo

    @PUT("$PATH/projects/{projectId}/billingInfo")
    suspend fun updateBillingInfo(
        @Path("projectId") projectId: String,
        @Body updateBillingInfoRequest: UpdateBillingInfoRequest
    ): ProjectBillingInfo

    @GET("$PATH/billingAccounts")
    suspend fun getBillingAccounts(
        @Query("pageToken") pageToken: String?,
        @Query("pageSize") pageSize: Int = 5
    ): BillingAccountResponse
}
