package com.batuhan.core.data.source.remote.management

import com.batuhan.core.data.model.FirebaseProject
import com.batuhan.core.data.model.FirebaseProjectResponse
import com.batuhan.core.data.model.management.*
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

    @GET("$PATH_VERSION/projects/{projectId}")
    suspend fun getProject(@Path("projectId") projectId: String): FirebaseProject

    @POST("$PATH_VERSION/{name}:addFirebase")
    suspend fun addFirebase(@Path("name", encoded = true) projectName: String): Operation

    @POST("$PATH_VERSION/{name}:addGoogleAnalytics")
    suspend fun addGoogleAnalytics(
        @Path("name", encoded = true) projectName: String,
        @Body addGoogleAnalyticsRequest: AddGoogleAnalyticsRequest
    ): Operation

    @GET("$PATH_VERSION/{name}/adminSdkConfig")
    suspend fun getAdminSDKConfig(@Path("name", encoded = true) projectName: String)

    @GET("$PATH_VERSION/projects/{projectId}/analyticsDetails")
    suspend fun getAnalyticsDetails(@Path("projectId") projectId: String): AnalyticsDetailsResponse

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

    @GET("$PATH_VERSION/projects/{projectId}/androidApps")
    suspend fun getAndroidApps(
        @Path("projectId") projectId: String,
        @Query("pageToken") pageToken: String? = null,
        @Query("pageSize") pageSize: Int = 5
    ): AndroidAppResponse

    @GET("$PATH_VERSION/projects/{projectId}/iosApps")
    suspend fun getIosApps(
        @Path("projectId") projectId: String,
        @Query("pageToken") pageToken: String? = null,
        @Query("pageSize") pageSize: Int = 5
    ): IosAppResponse

    @GET("$PATH_VERSION/projects/{projectId}/webApps")
    suspend fun getWebApps(
        @Path("projectId") projectId: String,
        @Query("pageToken") pageToken: String? = null,
        @Query("pageSize") pageSize: Int = 5
    ): WebAppResponse

    @GET("$PATH_VERSION/projects/{projectId}/androidApps/{appId}/config")
    suspend fun getAndroidConfig(
        @Path("projectId") projectId: String,
        @Path("appId") appId: String
    ): AndroidConfig

    @GET("$PATH_VERSION/projects/{projectId}/webApps/{appId}/config")
    suspend fun getWebConfig(
        @Path("projectId") projectId: String,
        @Path("appId") appId: String
    ): WebConfig

    @GET("$PATH_VERSION/projects/{projectId}/iosApps/{appId}/config")
    suspend fun getIosConfig(
        @Path("projectId") projectId: String,
        @Path("appId") appId: String
    ): IosConfig

    @POST("$PATH_VERSION/projects/{projectId}/androidApps")
    suspend fun createAndroidApp(
        @Path("projectId") projectId: String,
        @Body androidApp: AndroidApp
    ): Operation

    @POST("$PATH_VERSION/projects/{projectId}/iosApps")
    suspend fun createIosApp(
        @Path("projectId") projectId: String,
        @Body iosApp: IosApp
    ): Operation

    @POST("$PATH_VERSION/projects/{projectId}/webApps")
    suspend fun createWebApp(
        @Path("projectId") projectId: String,
        @Body webApp: WebApp
    ): Operation

    @PATCH("$PATH_VERSION/projects/{projectId}")
    suspend fun updateFirebaseProject(
        @Path("projectId") projectId: String,
        @Query("updateMask") updateMask: String,
        @Body updateFirebaseProjectRequest: UpdateFirebaseProjectRequest
    ): FirebaseProject

    @POST("$PATH_VERSION/projects/{projectId}:removeAnalytics")
    suspend fun removeGoogleAnalytics(
        @Path("projectId") projectId: String,
        @Body removeGoogleAnalyticsRequest: RemoveGoogleAnalyticsRequest
    )
}
