package com.batuhan.management.data.source.remote.firebase

import com.batuhan.management.data.model.AddGoogleAnalyticsRequest
import com.batuhan.management.data.model.FinalizeLocationRequest
import javax.inject.Inject

class ManagementRemoteDataSource @Inject constructor(private val managementService: ManagementService) {

    suspend fun getProject(projectName: String) = managementService.getProject(projectName)

    suspend fun addFirebase(projectName: String) = managementService.addFirebase(projectName)

    suspend fun addGoogleAnalytics(
        projectName: String,
        addGoogleAnalyticsRequest: AddGoogleAnalyticsRequest
    ) = managementService.addGoogleAnalytics(projectName, addGoogleAnalyticsRequest)

    suspend fun getAdminSDKConfig(projectName: String) =
        managementService.getAdminSDKConfig(projectName)

    suspend fun getAnalyticsDetails(projectName: String) =
        managementService.getAnalyticsDetails(projectName)

    suspend fun finalizeLocation(projectId: String, finalizeLocationRequest: FinalizeLocationRequest) =
        managementService.finalizeLocation(projectId, finalizeLocationRequest)

    suspend fun getFirebaseOperation(operationId: String) = managementService.getFirebaseOperation(operationId)
}
