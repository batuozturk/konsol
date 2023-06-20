package com.batuhan.management.data.repository

import androidx.paging.PagingData
import com.batuhan.core.data.model.FirebaseProject
import com.batuhan.management.data.model.*
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

    suspend fun getAnalyticsDetails(projectName: String)

    fun getAvailableProjects(): Flow<PagingData<ProjectInfo>>

    suspend fun createGoogleCloudProject(createProjectRequest: CreateProjectRequest): Operation

    suspend fun getGoogleCloudOperation(name: String): Operation

    suspend fun getGoogleAnalyticsAccounts(): AnalyticsAccountResponse

    suspend fun finalizeLocation(
        projectId: String,
        finalizeLocationRequest: FinalizeLocationRequest
    ): Operation

    fun getAvailableLocations(projectName: String): Flow<PagingData<AvailableLocation>>

    suspend fun getFirebaseOperation(operationId: String): Operation
}
