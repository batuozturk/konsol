package com.batuhan.core.data.source.remote.management.googlecloud

import com.batuhan.core.data.model.management.CreateProjectRequest
import javax.inject.Inject

class GoogleCloudRemoteDataSource @Inject constructor(private val service: GoogleCloudService) {

    suspend fun createGoogleCloudProject(createProjectRequest: CreateProjectRequest) =
        service.createGoogleCloudProject(createProjectRequest)

    suspend fun getGoogleCloudOperation(name: String) = service.getGoogleCloudOperation(name)

    suspend fun deleteGoogleCloudProject(projectId: String) =
        service.deleteGoogleCloudProject(projectId)
}
