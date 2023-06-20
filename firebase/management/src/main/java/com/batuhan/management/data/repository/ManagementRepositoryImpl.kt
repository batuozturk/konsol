package com.batuhan.management.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.batuhan.core.data.model.FirebaseProject
import com.batuhan.management.data.model.*
import com.batuhan.management.data.source.remote.firebase.*
import com.batuhan.management.data.source.remote.googleanalytics.GoogleAnalyticsRemoteDataSource
import com.batuhan.management.data.source.remote.googlecloud.GoogleCloudRemoteDataSource
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ManagementRepositoryImpl @Inject constructor(
    private val remoteDataSource: ManagementRemoteDataSource,
    private val managementService: ManagementService,
    private val googleCloudRemoteDataSource: GoogleCloudRemoteDataSource,
    private val googleAnalyticsRemoteDataSource: GoogleAnalyticsRemoteDataSource,
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

    override suspend fun getAnalyticsDetails(projectName: String) =
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

    override suspend fun getFirebaseOperation(operationId: String) = remoteDataSource.getFirebaseOperation(operationId)
}
