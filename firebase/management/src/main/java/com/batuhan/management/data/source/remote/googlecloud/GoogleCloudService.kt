package com.batuhan.management.data.source.remote.googlecloud

import com.batuhan.management.data.model.CreateProjectRequest
import com.batuhan.management.data.model.Operation
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
