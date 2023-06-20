package com.batuhan.management.data.source.remote.firebase

import com.batuhan.core.data.model.FirebaseProject
import com.batuhan.core.data.model.FirebaseProjectResponse
import com.batuhan.management.data.model.*
import retrofit2.http.*

interface ManagementService {

    companion object {
        private const val PATH_VERSION = "v1beta1"
    }

    @GET("$PATH_VERSION/projects")
    suspend fun getProjects(
        @Query("pageToken") pageToken: String? = null,
        @Query("pageSize") pageSize: Int = 5
    ): FirebaseProjectResponse

    @GET("$PATH_VERSION/{name}")
    suspend fun getProject(@Path("name", encoded = true) projectName: String): FirebaseProject

    @POST("$PATH_VERSION/{name}:addFirebase")
    suspend fun addFirebase(@Path("name", encoded = true) projectName: String): Operation

    @POST("$PATH_VERSION/{name}:addGoogleAnalytics")
    suspend fun addGoogleAnalytics(
        @Path("name", encoded = true) projectName: String,
        @Body addGoogleAnalyticsRequest: AddGoogleAnalyticsRequest
    ): Operation

    @GET("$PATH_VERSION/{name}/adminSdkConfig")
    suspend fun getAdminSDKConfig(@Path("name", encoded = true) projectName: String)

    @GET("$PATH_VERSION/{name}/analyticsDetails")
    suspend fun getAnalyticsDetails(@Path("name", encoded = true) projectName: String)

    @GET("$PATH_VERSION/availableProjects")
    suspend fun getAvailableProjects(
        @Query("pageToken") pageToken: String? = null,
        @Query("pageSize") pageSize: Int = 5
    ): ProjectInfoResponse

    @GET("$PATH_VERSION/{name}/availableLocations")
    suspend fun getAvailableLocations(
        @Path("name", encoded = true) projectName: String,
        @Query("pageToken") pageToken: String? = null,
        @Query("pageSize") pageSize: Int = 5
    ): AvailableLocationResponse

    @POST("$PATH_VERSION/{name}/defaultLocation:finalize")
    suspend fun finalizeLocation(
        @Path("name", encoded = true) projectName: String,
        @Body finalizeLocationRequest: FinalizeLocationRequest
    ): Operation

    @GET("$PATH_VERSION/{operationId}")
    suspend fun getFirebaseOperation(
        @Path("operationId", encoded = true) operationId: String
    ): Operation
}
