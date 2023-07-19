package com.batuhan.management.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.batuhan.core.data.model.FirebaseProject
import com.batuhan.management.data.model.*
import com.batuhan.management.data.source.remote.firebase.*
import com.batuhan.management.data.source.remote.googleanalytics.GetAnalyticsAccountsPagingSource
import com.batuhan.management.data.source.remote.googleanalytics.GoogleAnalyticsRemoteDataSource
import com.batuhan.management.data.source.remote.googleanalytics.GoogleAnalyticsService
import com.batuhan.management.data.source.remote.googlecloud.GoogleCloudRemoteDataSource
import com.batuhan.management.data.source.remote.googlecloud.billing.GetBillingAccountsPagingSource
import com.batuhan.management.data.source.remote.googlecloud.billing.GoogleCloudBillingDataSource
import com.batuhan.management.data.source.remote.googlecloud.billing.GoogleCloudBillingService
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ManagementRepositoryImpl @Inject constructor(
    private val remoteDataSource: ManagementRemoteDataSource,
    private val managementService: ManagementService,
    private val googleCloudRemoteDataSource: GoogleCloudRemoteDataSource,
    private val googleAnalyticsRemoteDataSource: GoogleAnalyticsRemoteDataSource,
    private val googleAnalyticsService: GoogleAnalyticsService,
    private val googleCloudBillingDataSource: GoogleCloudBillingDataSource,
    private val googleCloudBillingService: GoogleCloudBillingService
) :
    ManagementRepository {
    override fun getProjects(): Flow<PagingData<FirebaseProject>> = Pager(
        config = PagingConfig(pageSize = 5, enablePlaceholders = false),
        pagingSourceFactory = { ProjectsPagingSource(managementService) }
    ).flow

    override suspend fun getProject(projectName: String): FirebaseProject =
        remoteDataSource.getProject(projectName)

    override suspend fun addFirebase(projectName: String): Operation =
        remoteDataSource.addFirebase(projectName)

    override suspend fun addGoogleAnalytics(
        projectName: String,
        addGoogleAnalyticsRequest: AddGoogleAnalyticsRequest
    ): Operation =
        remoteDataSource.addGoogleAnalytics(projectName, addGoogleAnalyticsRequest)

    override suspend fun getAdminSDKConfig(projectName: String) =
        remoteDataSource.getAdminSDKConfig(projectName)

    override suspend fun getAnalyticsDetails(projectName: String): AnalyticsDetailsResponse =
        remoteDataSource.getAnalyticsDetails(projectName)

    override fun getAvailableProjects(): Flow<PagingData<ProjectInfo>> = Pager(
        config = PagingConfig(pageSize = 5, enablePlaceholders = false),
        pagingSourceFactory = { AvailableProjectsPagingSource(managementService) }
    ).flow

    override suspend fun createGoogleCloudProject(createProjectRequest: CreateProjectRequest) =
        googleCloudRemoteDataSource.createGoogleCloudProject(createProjectRequest)

    override suspend fun getGoogleCloudOperation(name: String) =
        googleCloudRemoteDataSource.getGoogleCloudOperation(name)

    override suspend fun getGoogleAnalyticsAccounts(): AnalyticsAccountResponse =
        googleAnalyticsRemoteDataSource.getGoogleAnalyticsAccounts()

    override fun getAnalyticsAccounts(): Flow<PagingData<AnalyticsAccount>> = Pager(
        config = PagingConfig(pageSize = 1, enablePlaceholders = false),
        pagingSourceFactory = {
            GetAnalyticsAccountsPagingSource(googleAnalyticsService)
        }
    ).flow

    override suspend fun finalizeLocation(
        projectId: String,
        finalizeLocationRequest: FinalizeLocationRequest
    ) =
        remoteDataSource.finalizeLocation(projectId, finalizeLocationRequest)

    override fun getAvailableLocations(projectName: String): Flow<PagingData<AvailableLocation>> =
        Pager(
            PagingConfig(5, enablePlaceholders = false),
            pagingSourceFactory = {
                AvailableLocationsPagingSource(managementService).apply {
                    setProjectName(projectName)
                }
            }
        ).flow

    override suspend fun getFirebaseOperation(operationId: String) =
        remoteDataSource.getFirebaseOperation(operationId)

    override fun getAndroidApps(projectId: String): Flow<PagingData<AndroidApp>> =
        Pager(
            config = PagingConfig(5, enablePlaceholders = false),
            pagingSourceFactory = {
                GetAndroidAppsPagingSource(managementService).apply {
                    setProjectId(projectId)
                }
            }
        ).flow

    override fun getIosApps(projectId: String): Flow<PagingData<IosApp>> =
        Pager(
            config = PagingConfig(5, enablePlaceholders = false),
            pagingSourceFactory = {
                GetIosAppsPagingSource(managementService).apply {
                    setProjectId(projectId)
                }
            }
        ).flow

    override fun getWebApps(projectId: String): Flow<PagingData<WebApp>> =
        Pager(
            config = PagingConfig(5, enablePlaceholders = false),
            pagingSourceFactory = {
                GetWebAppsPagingSource(managementService).apply {
                    setProjectId(projectId)
                }
            }
        ).flow

    override suspend fun getAndroidConfig(projectId: String, appId: String) =
        remoteDataSource.getAndroidConfig(projectId, appId)

    override suspend fun getIosConfig(projectId: String, appId: String) =
        remoteDataSource.getIosConfig(projectId, appId)

    override suspend fun getWebConfig(projectId: String, appId: String) =
        remoteDataSource.getWebConfig(projectId, appId)

    override suspend fun createAndroidApp(projectId: String, androidApp: AndroidApp) =
        remoteDataSource.createAndroidApp(projectId, androidApp)

    override suspend fun createIosApp(projectId: String, iosApp: IosApp) =
        remoteDataSource.createIosApp(projectId, iosApp)

    override suspend fun createWebApp(projectId: String, webApp: WebApp) =
        remoteDataSource.createWebApp(projectId, webApp)

    override suspend fun updateBillingInfo(
        projectId: String,
        updateBillingInfoRequest: UpdateBillingInfoRequest
    ) =
        googleCloudBillingDataSource.updateBillingInfo(projectId, updateBillingInfoRequest)

    override suspend fun getBillingInfo(projectId: String): ProjectBillingInfo =
        googleCloudBillingDataSource.getBillingInfo(projectId)

    override suspend fun deleteGoogleCloudProject(projectId: String) =
        googleCloudRemoteDataSource.deleteGoogleCloudProject(projectId)

    override suspend fun updateFirebaseProject(
        projectId: String,
        updateMask: String,
        updateFirebaseProjectRequest: UpdateFirebaseProjectRequest
    ): FirebaseProject =
        remoteDataSource.updateFirebaseProject(projectId, updateMask, updateFirebaseProjectRequest)

    override fun getBillingAccounts(): Flow<PagingData<BillingAccount>> = Pager(
        config = PagingConfig(pageSize = 5, enablePlaceholders = false),
        pagingSourceFactory = {
            GetBillingAccountsPagingSource(googleCloudBillingService)
        }
    ).flow

    override suspend fun removeGoogleAnalytics(
        projectId: String,
        removeGoogleAnalyticsRequest: RemoveGoogleAnalyticsRequest
    ) = remoteDataSource.removeGoogleAnalytics(projectId, removeGoogleAnalyticsRequest)
}
