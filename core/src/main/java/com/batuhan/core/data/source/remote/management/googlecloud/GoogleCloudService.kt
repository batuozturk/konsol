package com.batuhan.core.data.source.remote.management.googlecloud

import com.batuhan.core.data.model.management.CreateProjectRequest
import com.batuhan.core.data.model.management.Operation
import retrofit2.http.*

interface GoogleCloudService {

    companion object {
        private const val PATH = "v1"
    }

    @POST("$PATH/projects")
    suspend fun createGoogleCloudProject(@Body createProjectRequest: CreateProjectRequest): Operation

    @GET("$PATH/{name}")
    suspend fun getGoogleCloudOperation(@Path("name", encoded = true) name: String): Operation

    @DELETE("$PATH/projects/{projectId}")
    suspend fun deleteGoogleCloudProject(@Path("projectId") projectId: String)
}
