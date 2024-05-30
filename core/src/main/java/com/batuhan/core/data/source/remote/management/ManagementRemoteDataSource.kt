package com.batuhan.core.data.source.remote.management

import com.batuhan.core.data.model.management.*
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

    suspend fun finalizeLocation(
        projectId: String,
        finalizeLocationRequest: FinalizeLocationRequest
    ) = managementService.finalizeLocation(projectId, finalizeLocationRequest)

    suspend fun getFirebaseOperation(operationId: String) =
        managementService.getFirebaseOperation(operationId)

    suspend fun getAndroidConfig(projectId: String, appId: String) =
        managementService.getAndroidConfig(projectId, appId)

    suspend fun getIosConfig(projectId: String, appId: String) =
        managementService.getIosConfig(projectId, appId)

    suspend fun getWebConfig(projectId: String, appId: String) =
        managementService.getWebConfig(projectId, appId)

    suspend fun createAndroidApp(projectId: String, androidApp: AndroidApp) =
        managementService.createAndroidApp(projectId, androidApp)

    suspend fun createIosApp(projectId: String, iosApp: IosApp) =
        managementService.createIosApp(projectId, iosApp)

    suspend fun createWebApp(projectId: String, webApp: WebApp) =
        managementService.createWebApp(projectId, webApp)

    suspend fun updateFirebaseProject(projectId: String, updateMask: String, updateFirebaseProjectRequest: UpdateFirebaseProjectRequest) =
        managementService.updateFirebaseProject(projectId, updateMask, updateFirebaseProjectRequest)

    suspend fun removeGoogleAnalytics(
        projectId: String,
        removeGoogleAnalyticsRequest: RemoveGoogleAnalyticsRequest
    ) = managementService.removeGoogleAnalytics(projectId, removeGoogleAnalyticsRequest)
}
