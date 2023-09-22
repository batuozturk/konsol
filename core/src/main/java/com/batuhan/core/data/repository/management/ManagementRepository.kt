package com.batuhan.core.data.repository.management

import androidx.paging.PagingData
import com.batuhan.core.data.model.FirebaseProject
import com.batuhan.core.data.model.management.*
import kotlinx.coroutines.flow.Flow

interface ManagementRepository {

    fun getProjects(): Flow<PagingData<FirebaseProject>>

    suspend fun getProject(projectName: String): FirebaseProject

    suspend fun addFirebase(projectName: String): Operation

    suspend fun addGoogleAnalytics(
        projectName: String,
        addGoogleAnalyticsRequest: AddGoogleAnalyticsRequest
    ): Operation

    suspend fun getAdminSDKConfig(projectName: String)

    suspend fun getAnalyticsDetails(projectName: String): AnalyticsDetailsResponse

    fun getAvailableProjects(): Flow<PagingData<ProjectInfo>>

    suspend fun createGoogleCloudProject(createProjectRequest: CreateProjectRequest): Operation

    suspend fun getGoogleCloudOperation(name: String): Operation

    suspend fun getGoogleAnalyticsAccounts(): AnalyticsAccountResponse

    fun getAnalyticsAccounts(): Flow<PagingData<AnalyticsAccount>>

    suspend fun finalizeLocation(
        projectId: String,
        finalizeLocationRequest: FinalizeLocationRequest
    ): Operation

    fun getAvailableLocations(projectName: String): Flow<PagingData<AvailableLocation>>

    suspend fun getFirebaseOperation(operationId: String): Operation

    fun getAndroidApps(projectId: String): Flow<PagingData<AndroidApp>>

    fun getIosApps(projectId: String): Flow<PagingData<IosApp>>

    fun getWebApps(projectId: String): Flow<PagingData<WebApp>>

    suspend fun getAndroidConfig(projectId: String, appId: String): AndroidConfig

    suspend fun getIosConfig(projectId: String, appId: String): IosConfig

    suspend fun getWebConfig(projectId: String, appId: String): WebConfig

    suspend fun createAndroidApp(projectId: String, androidApp: AndroidApp): Operation

    suspend fun createIosApp(projectId: String, iosApp: IosApp): Operation

    suspend fun createWebApp(projectId: String, webApp: WebApp): Operation

    suspend fun updateBillingInfo(
        projectId: String,
        updateBillingInfoRequest: UpdateBillingInfoRequest
    ): ProjectBillingInfo

    suspend fun getBillingInfo(projectId: String): ProjectBillingInfo

    suspend fun deleteGoogleCloudProject(projectId: String)

    suspend fun updateFirebaseProject(
        projectId: String,
        updateMask: String,
        updateFirebaseProjectRequest: UpdateFirebaseProjectRequest
    ): FirebaseProject

    fun getBillingAccounts(): Flow<PagingData<BillingAccount>>

    suspend fun removeGoogleAnalytics(
        projectId: String,
        removeGoogleAnalyticsRequest: RemoveGoogleAnalyticsRequest
    )
}
