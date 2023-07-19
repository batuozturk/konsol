package com.batuhan.management.data.source.remote.googlecloud

import com.batuhan.management.data.model.CreateProjectRequest
import javax.inject.Inject

class GoogleCloudRemoteDataSource @Inject constructor(private val service: GoogleCloudService) {

    suspend fun createGoogleCloudProject(createProjectRequest: CreateProjectRequest) =
        service.createGoogleCloudProject(createProjectRequest)

    suspend fun getGoogleCloudOperation(name: String) = service.getGoogleCloudOperation(name)

    suspend fun deleteGoogleCloudProject(projectId: String) =
        service.deleteGoogleCloudProject(projectId)
}
